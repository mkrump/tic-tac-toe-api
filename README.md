# Tic-tac-toe API

Serves endpoints `valid-move`, `computer-move` at [https://tic-tac-toe-clojure.herokuapp.com/](https://tic-tac-toe-clojure.herokuapp.com/). These endpoints provide the game logic for [https://github.com/mkrump/tic-tac-toe-react](https://github.com/mkrump/tic-tac-toe-react). The Tic-tac-toe related functions are supplied by [https://github.com/mkrump/tic-tac-toe-clojure](https://github.com/mkrump/tic-tac-toe-clojure).  

## Requirements

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Tests

    lein test

## Running

To start a web server for the application locally, run:

    lein ring server   

## Example Requests

#### computer-move
Takes the current game state and returns an updated game state which includes the suggested move for a given position.

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
      "game-over": false,
      "current-player": -1
  }
}'
```

#### valid-move
Takes the current game state and a potential move and returns an updated game state if the move was valid.

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
         "game-over": false,
         "current-player": -1
     },
     "move": 2 
     }'
```

