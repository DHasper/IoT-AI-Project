// ==UserScript==
// @name         Connect Four Bot
// @namespace    http://tampermonkey.net/
// @version      1.0
// @description  Bot for connect four on the website papergames.io
// @author       Dylan Hasperhoven
// @match        https://papergames.io/en/*
// @require     https://cdn.jsdelivr.net/npm/axios@0.19.0/dist/axios.min.js
// @require     https://cdn.jsdelivr.net/npm/axios-userscript-adapter@0.0.4/dist/axiosGmxhrAdapter.min.js
// @grant       GM_xmlhttpRequest
// ==/UserScript==

axios.defaults.adapter = axiosGmxhrAdapter;

(async function() {
    'use strict';

    console.log('Connect 4 bot is active!');

    const botName = 'i win';
    const instaStart = true;
    let player;
    let inGame = false;
    let turn = 0;
    let moves = "";
    let board = [0, 0, 0, 0, 0, 0, 0,
                 0, 0, 0, 0, 0, 0, 0,
                 0, 0, 0, 0, 0, 0, 0,
                 0, 0, 0, 0, 0, 0, 0,
                 0, 0, 0, 0, 0, 0, 0,
                 0, 0, 0, 0, 0, 0, 0];

    while(true){
        await new Promise( resolve => {
            setTimeout( async function() {

                if(instaStart){
                    // Redirect to main page if the game is over
                    let restartButton = document.getElementsByClassName("btn btn-success big");
                    if(restartButton.length > 0 && restartButton[0].children[0].innerHTML.includes("Play")){
                        window.location.href = "https://papergames.io/en/connect4";
                    }

                    // CLick the play button if it is visible
                    let playButton = document.getElementsByClassName("btn btn-success btn-lg mb-2 ng-binding ng-isolate-scope");
                    if(playButton.length > 0){
                        playButton[0].click();
                    }

                    // Close the who are you menu if it pops up
                    let closeButton = document.getElementsByClassName("md-fab md-primary md-mini md-dialog-close md-button md-ink-ripple");
                    if(closeButton.length > 0 && closeButton[0].children[0].innerHTML.includes("close")){
                        closeButton[0].click();
                    }
                }
                

                // Check if we are in a game
                const game = document.getElementsByClassName("board");
                if(game.length > 0 && !inGame){
                    inGame = true;
                    let p1 = document.getElementsByClassName("player ng-scope player-one highlight");
                    if (p1.length == 0) p1 = (document.getElementsByClassName("player ng-scope player-one"));
                    
                    while(p1.length == 0){
                        await new Promise( resolveT => {
                            setTimeout( () => {
                                resolveT();
                            }, 500);
                        });
                    }

                    const username = p1[0].getElementsByClassName("username")[0].getElementsByClassName("ng-binding")[0].innerHTML;
                    player = username == botName ? 1 : 2;
                    startGame();
                }

                resolve();

            }, 100);
        });
    }

    async function startGame() {
        let p1Move, p2Move;
        console.log('We are now in a game and we are player ' + player);

        while(true){
            await new Promise( resolve => {
                setTimeout( () => {
                    let p1 = document.getElementsByClassName("player ng-scope player-one highlight");
                    let p2 = document.getElementsByClassName("player ng-scope player-two highlight");

                    if(p1.length != 0 && p2.length == 0 && !p1Move){
                        p2Move = false;
                        p1Move = true;
                        if(player == 1) doTurn();
                        turn++;
                    } else if(p2.length != 0 && p1.length == 0 && !p2Move){
                        p2Move = true;
                        p1Move = false;
                        if(player == 2) doTurn();
                        turn++;
                    }
    
                    resolve();
            
                }, 250);
            });
        }
    }

    function doTurn(){
        console.log('It is our turn');

        const opColor = player == 1 ? 'dark' : 'light';

        if(turn != 0){
            for(let row=0; row<6; row++){
                for(let col=0; col<7; col++){
                    const cell = document.getElementsByClassName(`cell-${row}-${col}`)[0].children[0].innerHTML;
                    const pos = col + (row*7);
                    if(cell.includes(opColor) && board[pos] == 0){
                        board[pos] = 3 - player;
                        moves = moves + col;
                    }
                }
            }
        }

        getMove(moves).then( res => {
            console.log(res);
            document.getElementsByClassName(`cell-${0}-${res}`)[0].click();
            moves = moves + res;
        });

        console.log(moves);
        console.log(board);
    }

    function getMove(movesStr){
        return new Promise( (resolve, reject) => {

            axios.get(`http://localhost:8080/getmove?pos=${movesStr}`)
            .then( res => {
                resolve(res.data);
            })
            .catch( err => {
                reject(err);
            });
        });
    }

})();