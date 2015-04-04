(function($) {
  var punModel = {
    puns: ko.observableArray([
      {
        id: 'aaa',
        author: 'Antonio',
        content: 'Pun 1 Pun 1 Pun 1'
      },
      {
        id: 'bbb',
        author: 'Sid',
        content: 'Pun 2 Pun 2 Pun 2'
      }
    ])
  };

  $(document).ready(function() {
    ko.applyBindings(punModel, $(".punstream")[0]);
  });
})(jQuery);
