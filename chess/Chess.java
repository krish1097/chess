package chess;

import chess.ReturnPiece.PieceFile;
import chess.ReturnPiece.PieceType;
import java.util.ArrayList;

public class Chess {

        enum Player { white, black }

		private static Player cplayer = Player.white;
		static ArrayList<ReturnPiece> pieces = new ArrayList<>();


		
    
	/**
	 * Plays the next move for whichever player has the turn.
	 * 
	 * @param move String for next move, e.g. "a2 a3"
	 * 
	 * @return A ReturnPlay instance that contains the result of the move.
	 *         See the section "The Chess class" in the assignment description for details of
	 *         the contents of the returned ReturnPlay instance.
	 */
	public static ReturnPlay play(String move) {

		/* FILL IN THIS METHOD */
		
		/* FOLLOWING LINE IS A PLACEHOLDER TO MAKE COMPILER HAPPY */
		/* WHEN YOU FILL IN THIS METHOD, YOU NEED TO RETURN A ReturnPlay OBJECT */
		ReturnPlay returnPlay = new ReturnPlay();
		

		if(move.equals("resign")){
			if(cplayer == Player.white){
				returnPlay.message = ReturnPlay.Message.RESIGN_BLACK_WINS;
				returnPlay.piecesOnBoard = pieces;
				return returnPlay;
			}
			returnPlay.message = ReturnPlay.Message.RESIGN_WHITE_WINS;
			return returnPlay;
		}

		String[] moves = move.split(" ");
		if(moves.length>2 && moves[2].equals("draw?")){
			returnPlay.message = ReturnPlay.Message.DRAW;
			return returnPlay;
		}

		String start = moves[0];
		String end = moves[1];
		

		//change turns after move is completed
		if(cplayer == Player.white){
			cplayer = Player.black;
		}else{ cplayer = Player.white;}
		return returnPlay;
	}
	
	
	/**
	 * This method should reset the game, and start from scratch.
	 */
	public static void start() {
		/* FILL IN THIS METHOD */
		//create the pawns
		for(int i = 1; i < 9; i++){
			Pawn wp= new Pawn(PieceType.WP, PieceFile.a, 2);
			Pawn bp= new Pawn(PieceType.WP, PieceFile.a, 7);

			switch(i){
                case 1:
					wp = new Pawn(PieceType.WP, PieceFile.a, 2);
					bp = new Pawn(PieceType.WP, PieceFile.a, 7);
					break;
                case 2:
					wp = new Pawn(PieceType.WP, PieceFile.b, 2);
					bp = new Pawn(PieceType.WP, PieceFile.b, 7);
					break;
                case 3:
					wp = new Pawn(PieceType.WP, PieceFile.c, 2);
					bp = new Pawn(PieceType.WP, PieceFile.c, 7);
					break;
                case 4:
					wp = new Pawn(PieceType.WP, PieceFile.d, 2);
					bp = new Pawn(PieceType.WP, PieceFile.d, 7);
					break;
                case 5:
					wp = new Pawn(PieceType.WP, PieceFile.e, 2);
					bp = new Pawn(PieceType.WP, PieceFile.e, 7);
					break;
                case 6:
					wp = new Pawn(PieceType.WP, PieceFile.f, 2);
					bp = new Pawn(PieceType.WP, PieceFile.f, 7);
					break;
                case 7:
					wp = new Pawn(PieceType.WP, PieceFile.g, 2);
					bp = new Pawn(PieceType.WP, PieceFile.g, 7);
					break;
                case 8:
					wp = new Pawn(PieceType.WP, PieceFile.h, 2);
					bp = new Pawn(PieceType.WP, PieceFile.h, 7);
					break;
            }
			pieces.add(wp);
			pieces.add(bp);
		}

		// add kings to array
		pieces.add(new King(PieceType.WK, PieceFile.e, 1));
		pieces.add(new King(PieceType.BK, PieceFile.e, 8));
		

		// add queens to array 
		pieces.add(new Queen(PieceType.WQ, PieceFile.d, 1));
		pieces.add(new Queen(PieceType.BQ, PieceFile.d, 8));

		// add Bishops to array 
		pieces.add(new Bishop(PieceType.WB, PieceFile.c, 1));
		pieces.add(new Bishop(PieceType.BB, PieceFile.c, 8));
		pieces.add(new Bishop(PieceType.WB, PieceFile.f, 1));
		pieces.add(new Bishop(PieceType.BB, PieceFile.f, 8));

		// add Knights to array 
		pieces.add(new Knight(PieceType.WN, PieceFile.b, 1));
		pieces.add(new Knight(PieceType.BN, PieceFile.b, 8));
		pieces.add(new Knight(PieceType.WN, PieceFile.g, 1));
		pieces.add(new Knight(PieceType.BN, PieceFile.g, 8));

		// add Rooks to array 
		pieces.add(new Rook(PieceType.WR, PieceFile.a, 1));
		pieces.add(new Rook(PieceType.BR, PieceFile.a, 8));
		pieces.add(new Rook(PieceType.WR, PieceFile.h, 1));
		pieces.add(new Rook(PieceType.BR, PieceFile.h, 8));



	}
}
