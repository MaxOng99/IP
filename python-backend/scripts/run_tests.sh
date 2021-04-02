cd python-backend/

pip install -r scripts/requirements.txt

cd src/main
python -m pylint_runner -rcfile=scripts/.pylintrc

cd ../src/tests
python -m pytest