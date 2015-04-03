(function($) {
  var punModel = {
    puns: ko.observableArray([
      {
        id: 'aaa',
        title: 'Pun 1',
        content: 'Pun 1 Pun 1 Pun 1'
      },
      {
        id: 'bbb',
        title: 'Pun 2',
        content: 'Pun 2 Pun 2 Pun 2'
      }
    ])
  };

  $(document).ready(function() {
    ko.applyBindings(punModel, $(".punstream")[0]);
  });
})(jQuery);
