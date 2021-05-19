import os
import shutil
import persistence


class Transaction:

    tid = None
    filepath = None
    has_running_operations = False
    did_any_operation_failed = False

    def __init__(self, tid):
        self.tid = tid
        self.filepath = 'data/transactions/' + self.tid + '/'
        os.makedirs(self.filepath)

    def update(self, entity_name, set_values, conditions):
        data_rows = persistence.get_file_data('data/persisted/', entity_name)

        if conditions['id'] not in data_rows.keys():
            return False

        row = data_rows[conditions['id']]

        if not self.all_conditions_apply(row, conditions):
            return False

        self.temporarily_persist_entity_row(entity_name, row, set_values)

    def commit(self):
        files = os.listdir(self.filepath)
        modified_entities = list(map(lambda file_name: file_name.strip('.txt'), files))

        for entity in modified_entities:
            self.persist_entity(entity)

        shutil.rmtree(self.filepath)
        return True

    def rollback(self):
        try:
            shutil.rmtree(self.filepath)
            return True
        except:
            return False

    def all_conditions_apply(self, row, conditions):
        for condition_key in conditions:
            desired_value = conditions[condition_key]
            actual_value = row[condition_key]
            if not desired_value == actual_value:
                return False
        return True

    def temporarily_persist_entity_row(self, entity_name, row, new_values):
        new_row = {new_values['id']: self.build_new_row(row, new_values)}
        temporarily_rows = persistence.get_file_data(self.filepath, entity_name)

        temporarily_rows = self.merge_rows(temporarily_rows, new_row)
        persistence.write_file_data(self.filepath, entity_name, {entity_name: temporarily_rows})

    def build_new_row(self, row, new_values):
        for value_kew in new_values:
            row[value_kew] = new_values[value_kew]
        return new_values

    def persist_entity(self, entity_name):
        modified_rows = persistence.get_file_data(self.filepath, entity_name)
        actual_rows = persistence.get_file_data('data/persisted/', entity_name)

        new_rows = self.merge_rows(actual_rows, modified_rows)
        persistence.write_file_data('data/persisted/', entity_name, {entity_name: new_rows})

    def merge_rows(self, rows_1: dict, rows_2: dict):
        for row_key in rows_2:
            if row_key in rows_1.keys():
                rows_1[row_key] = rows_2[row_key]
                continue
            rows_1.update({row_key: rows_2[row_key]})
        return rows_1

    def can_commit(self):
        return not self.has_running_operations and not self.did_any_operation_failed
