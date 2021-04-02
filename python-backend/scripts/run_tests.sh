cd python-backend/

pip install -r scripts/requirements.txt
python -m pylint --rcfile=scripts/.pylintrc src/main/*.py

cd src/tests
python -m pytest