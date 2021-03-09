import firebase_admin
from firebase_admin import auth
from firebase_admin import credentials
from fastapi import FastAPI, Header
from firebase_admin.auth import InvalidIdTokenError

app = FastAPI()
admin_sdk_path = "C:/Users/Lenovo/google/ta_proj_admin_sdk_service_account_file.json"
cred = credentials.Certificate(admin_sdk_path)
firebase_admin.initialize_app(cred)

@app.get('/')
def index():
	return {'key': 'value'}

@app.get('/auth')
def authenticate(*, jwt_token: str = Header(None)):
	try:
		decoded_token = auth.verify_id_token(jwt_token)
		return {"uid": decoded_token['uid']}
	except InvalidIdTokenError as ex:
		return {'error': ex}