describe('Start Page', function() {
  beforeEach(function() {
    browser.get('#/');
  });

  it('should show name', function() {
    var hdr = element(by.css(".navbar-brand"));
    expect(hdr.getText()).toEqual("orchidae");
  });

  it('should show tabs', function() {
    var elements = element.all(by.css("ul.navbar-nav li a msg")).filter(
            function(elem) {
              return elem.isDisplayed()
            });
    expect(elements.get(0).getText()).toBe("Upload");
    expect(elements.get(1).getText()).toBe("Photostream");
    expect(elements.get(2).getText()).toBe("Albums");
    expect(elements.get(3).getText()).toBe("Login");
  });

  it('should show language flag', function() {
    expect(
            element(by.css("li.dropdown a.dropdown-toggle.flag-icon-us"))
                    .isPresent()).toBe(true);
  });
});
