app.controller('JournalController', function(JournalService){
    var self = this;
        self.journal = '';
    self.fetchJournal = function() {
        JournalService.fetchJournal().then (function(response){
            self.journal=response;
        });
    };

    self.fetchJournal();
});
