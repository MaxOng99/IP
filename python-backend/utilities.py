import pickle
from firebase_admin import auth
from pipeline import DataPreparer, Preprocessor, Evaluator


def get_model(application_uid, experiment_type):
	with open(f'ml_models/{experiment_type}/model{application_uid}.pkl', 'rb') as file:
		return pickle.load(file)

def authenticate(jwt_token):
	decoded_token = auth.verify_id_token(jwt_token)
	return decoded_token['uid']

def prepare_data(users_df, experiment_type):
	users_df = DataPreparer(users_df).users_df
	preprocessed_reaction_df = {uid: Preprocessor(user_df, experiment_type).df for uid, user_df in users_df.items()}
	return preprocessed_reaction_df

def evaluate(model, users_df, uid):
	evaluator = Evaluator(model, users_df, uid)
	for uid, df in users_df.items():
		print(f'{uid}: {df.columns}')

	evaluation_object = dict()

	for i in range(1, 5):
		evaluation_object[i] = evaluator.get_prediction_results(i)

	far, frr, eer = evaluator.evaluate()
	evaluation_object['far'] = far
	evaluation_object['frr'] = frr
	evaluation_object['eer'] = eer

	return evaluation_object 

def get_uid_mapping():
	mapping = {'Ab95qgUrJDe0RNnT5A3ZIb5owEp1': 2,
	'HMav6t8045fX5g461hTtEVdgF4S2': 3,
	'QhMRTHq3FHcBDnJzOCcnmyltXKD2': 4,
	'd0BlMgUPQkQFPmAhVeCOaiWaqDB2': 1}
	return mapping




