# FastAPI imports
from fastapi import FastAPI, Header

# Firebase imports
import firebase_admin
from firebase_admin.auth import InvalidIdTokenError

# Custom module imports
from utilities.util_functions import authenticate
from utilities.util_functions import get_model, prepare_data, evaluate, get_uid_mapping
from api_structure.request import TouchGestureData, UserTGDMapping
from api_structure.response import EvaluationResult

## App Initialisation
default_app = firebase_admin.initialize_app()
app = FastAPI()

## API Routings
@app.get('/')
def index():
	return {'key': 'value'}

@app.post('/predict')
def predict(request: UserTGDMapping, jwt_token = Header(None)):

	try:
		jwt_uid = authenticate(jwt_token)
		application_uid = get_uid_mapping()[jwt_uid]
		model = get_model(application_uid, request.experiment_type)
		prepared_data = prepare_data(request.user_tgd_map, request.experiment_type)
		evaluation_object = evaluate(model, prepared_data, application_uid)
		return evaluation_object

	except InvalidIdTokenError:
		return "Authroization failed"