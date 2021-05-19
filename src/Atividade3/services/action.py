class Action:

    status = None
    function = None
    params = None

    def __init__(self, function, params):
        self.status = 'running'
        self.function = function
        self.params = params

    def perform(self):
        succeeded = self.function(self.params)
        if succeeded:
            self.status = 'finished'
            return

        self.status = 'failed'
