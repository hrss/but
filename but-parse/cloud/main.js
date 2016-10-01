require('cloud/app.js');
Parse.Cloud.define("getReviewQuotes", function(request, response) {
  var query = new Parse.Query("Review");
  var relation;
  var id;
  var promise = Parse.Promise.as();
  query.equalTo("objectId", request.params.id);
  query.find({
    success: function(results) {
      var _ = require('underscore');
      _.each(results, function(state) {
        promise = promise.then(function() {
          //get relation for each state
          var stateObject = state;
          relation = stateObject.relation("quotes");
          return relation.query().find();
        }).then(function(quotes) {
          response.success(quotes);
        });
      });
    },
    error: function(error) {
      error("Error: " + error.code + " " + error.message + " " + request.params.id);
    }
  });
});
Parse.Cloud.define("getPublicReviewsIds", function(request, response) {
  var query = new Parse.Query("Review");
  var userQuery = new Parse.Query("_User");
  getUser(request.params.id).then(
    //When the promise is fulfilled function(user) fires, and now we have our USER!
    function(user) {
      query.equalTo("user", user);
      query.find({
        success: function(results) {
          var ids = {};
          for (var i = 0; i < results.length; i++) {
            ids[i] = results[i].id;
          }
          response.success(ids);
        },
        error: function(error) {
          alert("Error: " + error.code + " " + error.message);
        }
      });
    },
    function(error) {
      response.error("Error: " + error.code + " " + error.message);
    });
});

function getUser(userId) {
  var userQuery = new Parse.Query(Parse.User);
  userQuery.equalTo("objectId", userId);
  //Here you aren't directly returning a user, but you are returning a function that will sometime in the future return a user. This is considered a promise.
  return userQuery.first({
    success: function(userRetrieved) {
      //When the success method fires and you return userRetrieved you fulfill the above promise, and the userRetrieved continues up the chain.
      return userRetrieved;
    },
    error: function(error) {
      return error;
    }
  });
};
Parse.Cloud.define("topQuotes", function(request, response) {
  var query = new Parse.Query("Review");
  var user;
  var quotesMap = {};
  var relation;
  query.equalTo("user", request.user);
  query.find().then(function(results) {
    var _ = require('underscore');
    var promise = Parse.Promise.as();
    var i = 0;
    _.each(results, function(state) {
      promise = promise.then(function() {
        //get relation for each state
        var stateObject = state;
        relation = stateObject.relation("quotes");
        return relation.query().find();
      }).then(function(quotes) {
        i++;
        _.each(quotes, function(quote) {
          if (quote.id in quotesMap) {
            quotesMap[quote.id].count += 1;
          } else {
            quotesMap[quote.id] = {
              count: 1,
              quote: quote
            };
          }
        });
      }).then(function() {
        if (i == results.length) {
          var sortable = [];
          for (var x in quotesMap) {
            sortable.push(quotesMap[x]);
          }
          sortable.sort(function(a, b) {
            return b.count - a.count
          });
          var resultList = [];
          for (var k = 0; k < 5 && k < sortable.length; k++) {
            resultList.push(sortable[k]);
          }
          response.success(resultList);
        }
      }, function(error) {
        error("Error: " + error.code + " " + error.message);
      });
    });
  });
});
Parse.Cloud.define("sendPush", function(request, response) {
  Parse.Cloud.useMasterKey();
  var senderUser = request.user;
  var senderUserId = senderUser.get("facebookId");
  var query = new Parse.Query("_User");
  query.equalTo("objectId", request.params.recipientUser);
  query.find({
    success: function(results) {
      var aTAuthQuery = new Parse.Query("ATAuth");
      var recipientUser = results[0];
      aTAuthQuery.equalTo("user", recipientUser);
      aTAuthQuery.find({
        success: function(tokens) {
          var token = tokens[0].get("token");
          Parse.Cloud.httpRequest({
            url: "https://graph.facebook.com/me/friends?fields=id&access_token=" + token
          }).then(function(httpResponse) {
            var j;
            var friends = JSON.parse(httpResponse.text);
            for (j = 0; j < friends.data.length; j++) {
              if (senderUserId == friends.data[j].id) {
                var pushQuery = new Parse.Query(Parse.Installation);
                pushQuery.equalTo("user", recipientUser);
                // Send push notification to query
                Parse.Push.send({
                  where: pushQuery,
                  data: {
                    alert: "You have a new review!"
                  }
                }).then(function() {
                  response.success("Push was sent successfully.");
                }, function(error) {
                  response.error("Push failed to send with error: " + error.message);
                });
              }
            }
          }, function(httpResponse) {
            console.error('Request failed with response code ' + httpResponse.status);
          });
        }
      });
    },
    error: function(error) {
      error("Error: " + error.code + " " + error.message);
    }
  });
});

Parse.Cloud.define("publishReview", function(request, response) {
  Parse.Cloud.useMasterKey();
  var senderUser = request.user;
  var Review = Parse.Object.extend("Review");
  var relation;
  var globalQuotes;
  var friendsIds = {};
  var senderName = senderUser.get("realName");
  var reviewId = request.params.review;
  var reviewQuery = new Parse.Query("Review");
  reviewQuery.get(reviewId, {
    success: function(review) {
      var relation = review.relation("quotes");
      var relationQuery = relation.query();

      var publicReviewACL = new Parse.ACL();
      publicReviewACL.setPublicReadAccess(true);

      review.setACL(publicReviewACL)

      review.save();

      console.log("after reviewQuery");
      relationQuery.find({
        success: function(quotes) {
          globalQuotes = quotes;
          var aTAuthFirstQuery = new Parse.Query("ATAuth");
          console.log("after relationQuery");
          aTAuthFirstQuery.equalTo("user", senderUser);
          aTAuthFirstQuery.descending("updatedAt");
          aTAuthFirstQuery.find({
            success: function(results3) {
              console.log("after ATAUthQuery");
              var atkFirst = results3[0];
              var tokenFirst = atkFirst.get("token");
              console.log(tokenFirst);

              Parse.Cloud.httpRequest({
                url: "https://graph.facebook.com/me/friends?fields=id&access_token=" + tokenFirst
              }).then(function(httpResponse) {
                var it;
                var myFirstArr = JSON.parse(httpResponse.text);
                for (it = 0; it < myFirstArr.data.length; it++) {
                  friendsIds[myFirstArr.data[it].id] = myFirstArr.data[it].id;
                }
                if (Object.keys(friendsIds).length < 501) {
                  var i = 0;
                  for (id in friendsIds) {
                    console.log(friendsIds);
                    var pushQuery = new Parse.Query(Parse.Installation);
                    var query = new Parse.Query("_User");
                    query.equalTo("facebookId", id);
                    query.find({
                      success: function(results) {
                        var recipientUser = results[0];
                        var newReview = new Review();
                      //  newReview.set("quotes", review.get("quotes"));
                        newReview.set("reviewOwner", senderUser);
                        newReview.set("user", recipientUser);
                        newReview.setACL(new Parse.ACL(recipientUser));
                        newReview.save(null, {
                          success: function(newReview) {
                            alert('New object created with objectId: ' + newReview.id);
                          },
                          error: function(newReview, error) {
                            alert('Failed to create new object, with error code: ' + error.message);
                          }
                        });
                        var newRelation = newReview.relation("quotes");
                        newRelation.add(globalQuotes);
                        newReview.save(null, {
                          success: function(newReview) {
                            alert('New relation created with objectId: ' + newReview.id);
                          },
                          error: function(newReview, error) {
                            alert('Failed to create new object, with error code: ' + error.message);
                          }
                        });
                        var pushQuery = new Parse.Query(Parse.Installation);
                        pushQuery.equalTo("user", recipientUser);
                        // Send push notification to query
                        Parse.Push.send({
                          where: pushQuery,
                          data: {
                            alert: "Your friend " + senderName + " published a review."
                          }
                        }).then(function() {
                          i++;
                          console.log("I'm here!" + i);
                          if (i == Object.keys(friendsIds).length) {
                            response.success("Push was sent successfully.");
                          }
                        }, function(error) {
                          i++;
                          if (i == Object.keys(friendsIds).length) response.error("Push failed to send with error: " + error.message);
                        });
                      },
                      error: function(error) {
                        error("Error: " + error.code + " " + error.message);
                      }
                    });
                  }
                } //////
              }, function(httpResponse) {
                // error
                console.error('Request failed with response code ' + httpResponse.status);
              });
            },
            error: function(error) {
              error("Error: " + error.code + " " + error.message);
            }
          }); ////
        },
        error: function(error) {
          alert("Error: " + error.code + " " + error.message);
        }
      });
    },
    error: function(object, error) {
      error("Error: " + error.code + " " + error.message);
    }
  });
});

Parse.Cloud.define("notifyFriends", function(request, response) {
  Parse.Cloud.useMasterKey();
  var senderUser = request.user;
  var friendsIds = {};
  var senderId = senderUser.get("facebookId");
  var senderName = senderUser.get("realName");
  var sentQuery = new Parse.Query("UserFirstNotificationSent");
  sentQuery.equalTo("user", senderUser);
  sentQuery.find({
    success: function(sent) {
      if (sent.length == 0) {
        var aTAuthFirstQuery = new Parse.Query("ATAuth");
        aTAuthFirstQuery.equalTo("user", senderUser);
        aTAuthFirstQuery.descending("updatedAt");
        aTAuthFirstQuery.find({
          success: function(results3) {
            var atkFirst = results3[0];
            var tokenFirst = atkFirst.get("token");
            Parse.Cloud.httpRequest({
              url: "https://graph.facebook.com/me/friends?fields=id&access_token=" + tokenFirst
            }).then(function(httpResponse) {
              var it;
              var myFirstArr = JSON.parse(httpResponse.text);
              for (it = 0; it < myFirstArr.data.length; it++) {
                friendsIds[myFirstArr.data[it].id] = myFirstArr.data[it].id;
              }
              if (Object.keys(friendsIds).length < 501) {
                var i = 0;
                for (id in friendsIds) {
                  var pushQuery = new Parse.Query(Parse.Installation);
                  var query = new Parse.Query("_User");
                  query.equalTo("facebookId", id);
                  query.find({
                    success: function(results) {
                      var recipientUser = results[0];
                      var pushQuery = new Parse.Query(Parse.Installation);
                      pushQuery.equalTo("user", recipientUser);
                      // Send push notification to query
                      Parse.Push.send({
                        where: pushQuery,
                        data: {
                          alert: "Your friend " + senderName + " is using But. Make a review!"
                        }
                      }).then(function() {
                        i++;
                        if (i == Object.keys(friendsIds).length) {
                          var UNot = new Parse.Object.extend("UserFirstNotificationSent");
                          var not = new UNot();
                          not.set("user", senderUser);
                          not.set("sent", true);
                          var acl = new Parse.ACL();
                          acl.setPublicReadAccess(false);
                          acl.setPublicWriteAccess(false);
                          not.setACL(acl);
                          not.save(null, {
                            success: function(not) {
                              alert('New object created with objectId: ' + not.id);
                            },
                            error: function(not, error) {
                              alert('Failed to create new object, with error code: ' + error.message);
                            }
                          });
                          response.success("Push was sent successfully.")
                        }
                      }, function(error) {
                        i++;
                        if (i == Object.keys(friendsIds).length) response.error("Push failed to send with error: " + error.message);
                      });
                    },
                    error: function(error) {
                      error("Error: " + error.code + " " + error.message);
                    }
                  });
                }
              } //////
            }, function(httpResponse) {
              // error
              console.error('Request failed with response code ' + httpResponse.status);
            });
          },
          error: function(error) {
            error("Error: " + error.code + " " + error.message);
          }
        }); ////
      }
    },
    error: function(error) {
      error("Error");
    }
  });
});
