import yaml
import json
import pandas as pd

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
            'add_statistical_features': self.add_statistical_features,
            'drop_redundant_features': self.drop_redundant_features
        }

        return mapping

    def preprocess(self):
        preprocess_steps = self.params['steps']

        for step in preprocess_steps:
            self.mapping[step]()

        self.reorder_columns()

    def load_params(self):
        with open("pipeline/params.yaml", 'r') as f:
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

        redundant_columns_regex = 'user|date|sample_id|tapCount.*|index.*|up.*|.*Timestamp.*|keyhold.*|intertap.*|avgkeyhold|avgintertap'
        essential_columns = self.df.columns.drop(list(self.df.filter(regex=redundant_columns_regex)))

        self.df = self.df[essential_columns]

    def add_statistical_features(self):
        features = self.params['statistical_features']

        for feature in features:
            temp = [f'{feature}{i}' for i in range(1, 4)] if feature == "intertap" else [f'{feature}{i}' for i in range(1, 5)]
            self.df.loc[:, f'avg{feature}'] = self.df[temp].apply(np.mean, axis=1)

    def reorder_columns(self):
        column_position = ['downPressure1', 'downX1', 'downY1', 
        'downPressure2', 'downX2', 'downY2',
        'downPressure3', 'downX3', 'downY3',
        'downPressure4', 'downX4', 'downY4',
        'keyhold1', 'keyhold2', 'keyhold3', 'keyhold4',
        'intertap1', 'intertap2', 'intertap3',
        'avgdownX', 'avgdownY', 'avgdownPressure', 'avgkeyhold', 'avgintertap']

        self.df = self.df.reindex(columns=column_position)