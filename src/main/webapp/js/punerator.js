(function($) {
  var punModel = {
    puns: ko.observableArray([]),
    queuedPuns: ko.observableArray([])
  }

  punModel.punsById = ko.computed(function() {
    var punsById = {},
        puns = punModel.puns();

    for (var index in puns) {
      var id = puns[index]._id();

      punsById[id] = puns[index];
    }

    return punsById;
  });

  punModel.numerOfQueuedPuns = ko.computed(function() {
    return punModel.queuedPuns().length;
  });

  $(document)
    .on('punstream-loaded', function(eventData) {
      for (var index in eventData.puns) {
        for (var key in eventData.puns[index]) {
          eventData.puns[index][key] = ko.observable(eventData.puns[index][key]);
        }
      }

      punModel.puns(eventData.puns);
      ko.applyBindings(punModel, $(".punstream")[0]);
    })
    .on('new-pun', function(eventData) {
      punModel.queuedPuns.prepend(eventData.pun);
    })
    .on('pun-vote', function(eventData) {
      if (eventData.voteType === 'punny') {
        punModel.punsById[eventData.punId].punny(
          punModel.punsById[eventData.punId].punny() + 1
        );
      } else if (eventData.voteType === 'tearable'){
        punModel.punsById[eventData.punId].tearable(
          punModel.punsById[eventData.punId].tearable() + 1
        );
      }
    });
})(jQuery);
