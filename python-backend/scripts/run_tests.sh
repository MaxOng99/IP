cd python-backend/

pip install -r scripts/requirements.txt
pylint --rcfile=scripts/.pylintrc src/main

cd src/tests
python -m pytest