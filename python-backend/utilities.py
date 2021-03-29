import pickle
from firebase_admin import auth
from pipeline import DataPreparer, Preprocessor, Evaluator


def get_model(uid, experiment_type):
	with open(f'ml_models/{experiment_type}/model{id}.pkl', 'rb') as file:
		return pickle.load(file)

def authenticate(jwt_token):
	decoded_token = auth.verify_id_token(jwt_token)
	return decoded_token['uid']

def prepare_data(users_df):
	df = DataPreparer(users_df)
	preprocessor = Preprocessor(df.users_df)
	return preprocessor.df

def evaluate(model, users_df, uid):
	evaluator = Evaluator(model, users_df, uid)
	evaluation_object = dict()

	for i in range(1, 5):
		evaluation_object[i] = evaluator.get_prediction_results(i)

	far, frr, eer = evaluator.evaluate()
	evaluation_object['far'] = far
	evaluation_object['frr'] = frr
	evaluation_object['eer'] = eer

	return evaluation_object 



