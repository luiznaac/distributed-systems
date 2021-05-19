import json


def get_file_data(filepath, entity_name) -> dict:
    try:
        file = open(filepath + entity_name + '.txt', mode='r', encoding='utf-8')
        return json.loads(file.read())[entity_name]
    except:
        return {}


def write_file_data(filepath, entity_name, contents):
    file = open(filepath + entity_name + '.txt', 'w+')
    file.write(json.dumps(contents, indent=2))
    file.close()
