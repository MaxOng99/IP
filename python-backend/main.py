# FastAPI imports
from fastapi import FastAPI, Header

# Firebase imports
import firebase_admin
from firebase_admin.auth import InvalidIdTokenError

# Custom module imports
from utilities import authenticate
from utilities import get_model, prepare_data, evaluate
from data_objects import TouchGestureData, UserTGDMapping, EvaluationResult

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
		uid = authenticate(jwt_token)

		model = get_model(uid)
		prepared_data = prepare_data(request)
		evaluation_object = evaluate(prepare_data, uid)
		return evaluation_object

	except InvalidIdTokenError:
		return "Authroization failed"


