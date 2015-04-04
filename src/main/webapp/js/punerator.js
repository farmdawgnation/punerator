(function($) {
  var punA = {
    id: 'aaa',
    author: 'Antonio',
    content: 'Pun 1 Pun 1 Pun 1'
  }

  var punB = {
    id: 'bbb',
    author: 'Sid',
    content: 'Pun 2 Pun 2 Pun 2'
  }

  var punModel = {
    puns: ko.observableArray([punA, punB]),
    punsById: {'aaa': punA, 'bbb', punB},
    queuedPuns: ko.observableArray([])
  };

  $(document)
    .on('punstream-loaded', function(eventData) {
      ko.applyBindings(punModel, $(".punstream")[0]);
    })
    .on('new-pun', function(eventData) {

    })
    .on('pun-vote', function(eventData) {

    });
})(jQuery);
