"""
module that provides utility functions that deal with
loading models, API usage authorization and encapsulation
of ML pipeline.
"""
import pickle
from firebase_admin import auth
from pipeline.data_preparer import DataPreparer
from pipeline.preprocessor import Preprocessor
from pipeline.evaluator import Evaluator

def get_model(application_uid, experiment_type):
	"""
	Returns the auth model of a participant
	based on a the experiment type (keystroke/reaction).
	"""
	with open(f'ml_models/{experiment_type}/model{application_uid}.pkl', 'rb') as file:
		return pickle.load(file)

def authenticate(jwt_token):
	"""Utilise firebase auth library to perform jwt_token verification."""
	decoded_token = auth.verify_id_token(jwt_token)
	return decoded_token['uid']

def prepare_data(users_df, experiment):
	"""
	Convert raw data to pandas compatbile format, apply
	feature enhancement and drops redundant features.
	"""
	users_df = DataPreparer(users_df).users_df
	preprocessed_df = {uid: Preprocessor(user_df, experiment).df for uid, user_df in users_df.items()}
	return preprocessed_df

# pylint: disable=C0116
def evaluate(model, users_df, uid):
	evaluator = Evaluator(model, users_df, uid)
	evaluation_object = dict()
	predictions = dict()

	for i in range(1, 5):
		predictions[i] = evaluator.get_prediction_results(i)

	evaluation_object['predictions'] = predictions
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
