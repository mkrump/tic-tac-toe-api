# tictactoe-api

Serves endpoints `valid-move`, `computer-move` at `https://tic-tac-toe-clojure.herokuapp.com/` that can be used to play tic-tac-toe. The tic-tac-toe related functions are supplied by [https://github.com/mkrump/tic-tac-toe-clojure](https://github.com/mkrump/tic-tac-toe-clojure).  

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Tests

    lein test

## Running

To start a web server for the application locally, run:

    lein ring server   

## Example Requests

##### computer-move

```
curl -X POST \
  https://tic-tac-toe-clojure.herokuapp.com/computer-move \
  -H 'content-type: application/json' \
  -d '{
  "game-state": {
      "board": {
          "board-contents": [1, 1, 0, 0, -1, 0, 0, 0, 0],
          "gridsize": 3
      },
      "winner": 0,
      "is-tie": false,
      "current-player": -1
  }
}'
```

##### valid-move
```
curl -X POST \
  https://tic-tac-toe-clojure.herokuapp.com/valid-move \
  -H 'content-type: application/json' \
  -d '{"game-state": {
         "board": {
             "board-contents": [ 1, 1, 0, 0, -1, 0, 0, 0, 0 ],
             "gridsize": 3
         },
         "winner": 0,
         "is-tie": false,
         "current-player": -1
     },
     "move": 2 
     }'
```

