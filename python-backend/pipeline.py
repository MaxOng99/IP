import numpy as np
import yaml
import json
import pandas as pd

class DataPreparer:
    def __init__(self, dataset):

        self.original_df = None
        self.users_df = None
        self.prepare_data(dataset)


    # Convert raw json data into compatible Pandas DataFrame format
    def prepare_data(self, dataset):
        user_ids = list(dataset.keys())
        userRecords = []

        for uid in user_ids:
            records = dataset[uid]

            for record in records:
                userRecords.append(self.flatten_record(uid, record))

        prepared_df = pd.read_json(json.dumps(userRecords))
        self.original_df = prepared_df
        self.users_df = {uid:usergroup.copy(deep=True) for uid, usergroup in prepared_df.groupby(by=['user'])}

    # Flattens nested json data
    def flatten_record(self, user, record):

        feature_map = dict()
        feature_map['user'] = user
        feature_map['date'] = record.date
        feature_map['sample_id'] = record.sample_id
        tap_data = record.data

        for index, single_tap in enumerate(tap_data):

            # Add tap count to each feature name to prevent overrides when JSON data is flattened
            new_dict = {f'{k}{index+1}':v for k,v in single_tap.items()}
            feature_map.update(new_dict)

        return feature_map

    def get_uid_mapping(self):
        mapping = {'Ab95qgUrJDe0RNnT5A3ZIb5owEp1': 2,
        'HMav6t8045fX5g461hTtEVdgF4S2': 3,
        'QhMRTHq3FHcBDnJzOCcnmyltXKD2': 4,
        'd0BlMgUPQkQFPmAhVeCOaiWaqDB2': 1}
        return mapping

class Preprocessor:
    def __init__(self, df, dataset_type):
        self.df = df
        self.dataset_type = dataset_type
        self.params = self.load_params()
        self.mapping = self.step_to_function_mapping()
        self.preprocess()

    def step_to_function_mapping(self):
        mapping = {
            'add_temporal_features': self.add_temporal_features,
            'drop_redundant_features': self.drop_redundant_features,
            'remove_outliers': self.remove_outliers,
            'add_statistical_features': self.add_statistical_features
        }

        return mapping

    def preprocess(self):
        preprocess_steps = self.params['steps']

        for step in preprocess_steps:
            self.mapping[step]()

    def load_params(self):
        with open("params.yaml", 'r') as f:
            return  yaml.safe_load(f)['preprocess']

    def add_temporal_features(self):

        if self.dataset_type == "reaction":
            for i, row in self.df.iterrows():
                new_row = self.reorder_timestamp(row)
                self.df.at[i, :] = new_row

        # Add keyhold features
        for i in range(1, 5):
            self.df.loc[:, f'keyhold{i}'] = self.df[f'upTimestamp{i}'] - self.df[f'downTimestamp{i}']

        # Add reaction features
        for j in range(1, 4):
            self.df.loc[:, f'intertap{j}'] = self.df[f'downTimestamp{j+1}'] - self.df[f'upTimestamp{j}']

        # Remove individual timestamp feature
        cols = self.df.columns.drop(list(self.df.filter(regex = 'upTimestamp.*|downTimestamp.*')))
        self.df = self.df[cols]

    # Used for adding temporal features for reaction dataset
    def reorder_timestamp(self, row):

        temp = row.copy(deep=True)

        for i in range(1, 5):
            newDown = row[f'downTimestamp{i}']
            newUp = row[f'upTimestamp{i}']

            if row[f'tapCount{i}'] == 0:
                row['downTimestamp1'] = newDown
                row['upTimestamp1'] = newUp

            elif row[f'tapCount{i}'] == 1:
                row['downTimestamp2'] = newDown
                row['upTimestamp2'] = newUp

            elif row[f'tapCount{i}'] == 2:
                row['downTimestamp3'] = newDown
                row['upTimestamp3'] = newUp

            elif row[f'tapCount{i}'] == 3:
                row['downTimestamp4'] = newDown
                row['upTimestamp4'] = newUp

        return row

    def drop_redundant_features(self):

        redundant_columns_regex = 'user|date|sampleId|tapCount.*|index.*|up.*'
        essential_columns = self.df.columns.drop(list(self.df.filter(regex=redundant_columns_regex)))

        self.df = self.df[essential_columns]

    def add_statistical_features(self):
        features = self.params['statistical_features']

        for feature in features:
            temp = [f'{feature}{i}' for i in range(1, 4)] if feature == "intertap" else [f'{feature}{i}' for i in range(1, 5)]
            self.df.loc[:, f'avg{feature}'] = self.df[temp].apply(np.mean, axis=1)

class Preprocessor:
    def __init__(self, df, dataset_type):
        self.df = df
        self.dataset_type = dataset_type
        self.params = self.load_params()
        self.mapping = self.step_to_function_mapping()
        self.preprocess()

    def step_to_function_mapping(self):
        mapping = {
            'add_temporal_features': self.add_temporal_features,
            'drop_redundant_features': self.drop_redundant_features,
            'remove_outliers': self.remove_outliers,
            'add_statistical_features': self.add_statistical_features
        }

        return mapping

    def preprocess(self):
        preprocess_steps = self.params['steps']

        for step in preprocess_steps:
            self.mapping[step]()

    def load_params(self):
        with open("params.yaml", 'r') as f:
            return  yaml.safe_load(f)['preprocess']

    def add_temporal_features(self):

        if self.dataset_type == "reaction":
            for i, row in self.df.iterrows():
                new_row = self.reorder_timestamp(row)
                self.df.at[i, :] = new_row

        # Add keyhold features
        for i in range(1, 5):
            self.df.loc[:, f'keyhold{i}'] = self.df[f'upTimestamp{i}'] - self.df[f'downTimestamp{i}']

        # Add reaction features
        for j in range(1, 4):
            self.df.loc[:, f'intertap{j}'] = self.df[f'downTimestamp{j+1}'] - self.df[f'upTimestamp{j}']

        # Remove individual timestamp feature
        cols = self.df.columns.drop(list(self.df.filter(regex = 'upTimestamp.*|downTimestamp.*')))
        self.df = self.df[cols]

    # Used for adding temporal features for reaction dataset
    def reorder_timestamp(self, row):

        temp = row.copy(deep=True)

        for i in range(1, 5):
            newDown = row[f'downTimestamp{i}']
            newUp = row[f'upTimestamp{i}']

            if row[f'tapCount{i}'] == 0:
                row['downTimestamp1'] = newDown
                row['upTimestamp1'] = newUp

            elif row[f'tapCount{i}'] == 1:
                row['downTimestamp2'] = newDown
                row['upTimestamp2'] = newUp

            elif row[f'tapCount{i}'] == 2:
                row['downTimestamp3'] = newDown
                row['upTimestamp3'] = newUp

            elif row[f'tapCount{i}'] == 3:
                row['downTimestamp4'] = newDown
                row['upTimestamp4'] = newUp

        return row

    def drop_redundant_features(self):

        redundant_columns_regex = 'user|date|sampleId|tapCount.*|index.*|up.*'
        essential_columns = self.df.columns.drop(list(self.df.filter(regex=redundant_columns_regex)))

        self.df = self.df[essential_columns]

    def remove_outliers(self):
        pass

    def add_statistical_features(self):
        features = self.params['statistical_features']

        for feature in features:
            temp = [f'{feature}{i}' for i in range(1, 4)] if feature == "intertap" else [f'{feature}{i}' for i in range(1, 5)]
            self.df.loc[:, f'avg{feature}'] = self.df[temp].apply(np.mean, axis=1)

class Evaluator():

    def __init__(self, model, users_df, legitimate_uid):
        self.model = model
        self.users_df = users_df
        self.uid = legitimate_uid

    def calculate_far(y_true, y_pred):
        cm = metrics.confusion_matrix(y_true, y_pred)
        fp = cm[0, 1] 
        tn = cm[0, 0] 
        far = fp/(fp+tn)

        return far

    def calculate_frr(y_true, y_pred):
        cm = metrics.confusion_matrix(y_true, y_pred)
        fn = cm[1, 0]
        tp = cm[1, 1]
        frr = fn/(fn+tp)

        return frr

    def calculate_eer(y_true, y_pred_score):
        fpr, fnr, thresholds = metrics.det_curve(y_true, y_score)
        eer1 = fpr[np.nanargmin(np.absolute(fnr - fpr))]
        eer2 = fnr[np.nanargmin(np.absolute(fnr - fpr))]
        eer = (eer1 + eer2)/2

        return eer

    def get_prediction_results(self, uid):

        X = self.users_df[uid]
        y_true = np.repeat(1.0, len(X)) if uid == self.uid else np.repeat(-1.0, len(X))
        y_pred = self.model.predict(X)
        accepted_predictions = [prediction for prediction in y_pred if prediction == 1.0]

        return len(accepted_predictions)

    def evaluate(self):

        legitimate_df = self.users_df[self.uid]
        imposters_df = pd.concat([imposter_df for uid, imposter_df in self.users_df.items() if uid != self.uid], axis=0, ignore_index=True)

        X = pd.concat([legitimate_df, imposter_df], axis=0, ignore_index=True)
        y = np.concatenate(np.repeat(1.0, len(legitimate_df)), np.repeat(-1.0, len(imposters_df)))
        y_predict = self.model.predict(X)
        y_predict_score = self.model.decision_function(X)

        
        far = self.calculate_far(y, y_predict)
        frr = self.calculate_frr(y, y_predict)
        eer = self.calculate_eer(y, y_predict_score)

        return (far, frr, eer)
