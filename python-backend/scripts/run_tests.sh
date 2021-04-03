cd python-backend/

pip install -r scripts/requirements.txt

cd src/main
pylint_runner -rcfile=.pylintrc

cd ../tests
python -m pytest