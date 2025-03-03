package chess;

import chess.ReturnPiece.PieceFile;
import chess.ReturnPiece.PieceType;
import java.util.ArrayList;

public class Chess {

        enum Player { white, black }

		private static Player cplayer = Player.white;
		private static boolean captured = false; 
		static ArrayList<ReturnPiece> pieces = new ArrayList<>();

		// Helper method to find a king's position
		private static String getKingPosition(boolean isWhite) {
			for(ReturnPiece piece : pieces) {
				if((isWhite && piece.pieceType == PieceType.WK) || 
				   (!isWhite && piece.pieceType == PieceType.BK)) {
					return "" + piece.pieceFile + piece.pieceRank;
				}
			}
			return null;
		}

		// Check if a position is under attack by the opponent
		private static boolean isPositionUnderAttack(String position, boolean byWhite) {
			// Create a copy of the pieces list to avoid concurrent modification
			ArrayList<ReturnPiece> piecesCopy = new ArrayList<>(pieces);
			
			for(ReturnPiece piece : piecesCopy) {
				boolean isPieceWhite = piece.pieceType.toString().charAt(0) == 'W';
				if(isPieceWhite == byWhite) {
					// Create a test move from the piece's position to the target position
					String testMove = "" + piece.pieceFile + piece.pieceRank + " " + position;
					
					// Store piece information before testing
					PieceFile originalFile = piece.pieceFile;
					int originalRank = piece.pieceRank;
					
					// Store the piece at the target position without removing it yet
					ReturnPiece capturedPiece = getPiece(position);
					
					// Test if the move would be legal without actually removing any pieces
					boolean canAttack = false;
					if(piece instanceof Pawn) {
						// Special case for pawns - they capture differently than they move
						int rankDiff = Math.abs(position.charAt(1) - '0' - piece.pieceRank);
						int fileDiff = Math.abs(position.charAt(0) - piece.pieceFile.toString().charAt(0));
						canAttack = rankDiff == 1 && fileDiff == 1;
					} else {
						// Temporarily move the piece
						piece.pieceFile = PieceFile.valueOf(String.valueOf(position.charAt(0)));
						piece.pieceRank = position.charAt(1) - '0';
						
						// Check if the move would be legal
						canAttack = isLegal(piece, testMove);
						
						// Restore the piece's original position
						piece.pieceFile = originalFile;
						piece.pieceRank = originalRank;
					}
					
					if(canAttack) {
						return true;
					}
				}
			}
			return false;
		}

		// Check if the specified color is in check
		private static boolean isInCheck(boolean isWhite) {
			String kingPos = getKingPosition(isWhite);
			return isPositionUnderAttack(kingPos, !isWhite);
		}

		// Check if the move would put the current player in check
		private static boolean moveCreatesCheck(ReturnPiece piece, String move) {
			boolean isWhite = piece.pieceType.toString().charAt(0) == 'W';
			String[] moves = move.split(" ");
			String end = moves[1];

			// Store the piece's original position
			PieceFile originalFile = piece.pieceFile;
			int originalRank = piece.pieceRank;
			
			// Store the captured piece without removing it
			ReturnPiece capturedPiece = getPiece(end);

			// Temporarily move the piece
			if(capturedPiece != null) {
				pieces.remove(capturedPiece);
			}
			piece.pieceFile = PieceFile.valueOf(String.valueOf(end.charAt(0)));
			piece.pieceRank = end.charAt(1) - '0';

			// Check if the move puts either king in check
			boolean createsCheck = isInCheck(isWhite);

			// Restore the original position
			piece.pieceFile = originalFile;
			piece.pieceRank = originalRank;
			if(capturedPiece != null) {
				pieces.add(capturedPiece);
			}

			return createsCheck;
		}

		// Check if the opponent is in checkmate
		private static boolean isCheckmate(boolean isWhiteInCheck) {
			// First verify that the king is in check
			if(!isInCheck(isWhiteInCheck)) {
				return false;
			}

			// Create a copy of the pieces list to avoid modification issues
			ArrayList<ReturnPiece> originalPieces = new ArrayList<>(pieces);

			// Try every possible move for every piece
			for(ReturnPiece piece : originalPieces) {
				boolean isPieceWhite = piece.pieceType.toString().charAt(0) == 'W';
				// Only check moves for pieces of the player in check
				if(isPieceWhite == isWhiteInCheck) {
					PieceFile originalFile = piece.pieceFile;
					int originalRank = piece.pieceRank;

					// Try moving to every square
					for(char file = 'a'; file <= 'h'; file++) {
						for(int rank = 1; rank <= 8; rank++) {
							String move = "" + piece.pieceFile + piece.pieceRank + " " + file + rank;
							
							// Store any piece at the destination
							ReturnPiece capturedPiece = getPiece("" + file + rank);

							// Try the move if it's legal
							if(isLegal(piece, move)) {
								// Temporarily make the move
								if(capturedPiece != null) {
									pieces.remove(capturedPiece);
								}
								piece.pieceFile = PieceFile.valueOf(String.valueOf(file));
								piece.pieceRank = rank;

								// Check if this move gets out of check
								boolean stillInCheck = isInCheck(isWhiteInCheck);

								// Restore the original position
								piece.pieceFile = originalFile;
								piece.pieceRank = originalRank;
								
								// Restore the captured piece if any
								if(capturedPiece != null) {
									pieces.add(capturedPiece);
								}

								// If we found a move that gets out of check, it's not checkmate
								if(!stillInCheck) {
									// Restore the original pieces list
									pieces.clear();
									pieces.addAll(originalPieces);
									return false;
								}
							}
						}
					}
				}
			}

			// Restore the original pieces list
			pieces.clear();
			pieces.addAll(originalPieces);
			
			// If we get here, no legal move was found that gets out of check
			return true;
		}

		// Handle pawn promotion
		private static void promotePawn(Pawn pawn, String end, String promotionPiece) {
			int rank = end.charAt(1) - '0';
			PieceFile file = PieceFile.valueOf(String.valueOf(end.charAt(0)));
			boolean isWhite = pawn.pieceType == PieceType.WP;
			
			// Remove the pawn
			pieces.remove(pawn);
			
			// Create the new piece
			ReturnPiece newPiece;
			if(promotionPiece != null && promotionPiece.equals("N")) {
				newPiece = new Knight(isWhite ? PieceType.WN : PieceType.BN, file, rank);
			} else if(promotionPiece != null && promotionPiece.equals("B")) {
				newPiece = new Bishop(isWhite ? PieceType.WB : PieceType.BB, file, rank);
			} else if(promotionPiece != null && promotionPiece.equals("R")) {
				newPiece = new Rook(isWhite ? PieceType.WR : PieceType.BR, file, rank);
			} else {
				newPiece = new Queen(isWhite ? PieceType.WQ : PieceType.BQ, file, rank);
			}
			
			pieces.add(newPiece);
		}

		// Handle castling
		private static boolean canCastle(King king, String move) {
			boolean isWhite = king.pieceType == PieceType.WK;
			String[] moves = move.split(" ");
			String start = moves[0];
			String end = moves[1];
			
			// Check if king has moved
			if(king.hasMoved()) {
				return false;
			}
			
			// Check if it's a castling move
			if(!end.equals(isWhite ? "g1" : "g8") && !end.equals(isWhite ? "c1" : "c8")) {
				return false;
			}
			
			// Check if king is in check
			if(isInCheck(isWhite)) {
				return false;
			}
			
			// Check kingside castling
			if(end.equals(isWhite ? "g1" : "g8")) {
				ReturnPiece rookPiece = getPiece(isWhite ? "h1" : "h8");
				if(!(rookPiece instanceof Rook)) {
					return false;
				}
				Rook rook = (Rook)rookPiece;
				if(rook.hasMoved()) {
					return false;
				}
				
				// Check if path is clear and not under attack
				if(getPiece(isWhite ? "f1" : "f8") != null || 
				   getPiece(isWhite ? "g1" : "g8") != null ||
				   isPositionUnderAttack(isWhite ? "f1" : "f8", !isWhite) ||
				   isPositionUnderAttack(isWhite ? "g1" : "g8", !isWhite)) {
					return false;
				}
				
				return true;
			}
			
			// Check queenside castling
			if(end.equals(isWhite ? "c1" : "c8")) {
				ReturnPiece rookPiece = getPiece(isWhite ? "a1" : "a8");
				if(!(rookPiece instanceof Rook)) {
					return false;
				}
				Rook rook = (Rook)rookPiece;
				if(rook.hasMoved()) {
					return false;
				}
				
				// Check if path is clear and not under attack
				if(getPiece(isWhite ? "d1" : "d8") != null || 
				   getPiece(isWhite ? "c1" : "c8") != null ||
				   getPiece(isWhite ? "b1" : "b8") != null ||
				   isPositionUnderAttack(isWhite ? "d1" : "d8", !isWhite) ||
				   isPositionUnderAttack(isWhite ? "c1" : "c8", !isWhite)) {
					return false;
				}
				
				return true;
			}
			
			return false;
		}

		// Execute castling move
		private static void doCastling(King king, String move) {
			boolean isWhite = king.pieceType == PieceType.WK;
			String[] moves = move.split(" ");
			String end = moves[1];
			
			// Kingside castling
			if(end.equals(isWhite ? "g1" : "g8")) {
				ReturnPiece rook = getPiece(isWhite ? "h1" : "h8");
				rook.pieceFile = PieceFile.valueOf(String.valueOf('f'));
				updateArraylist(rook);
			}
			// Queenside castling
			else if(end.equals(isWhite ? "c1" : "c8")) {
				ReturnPiece rook = getPiece(isWhite ? "a1" : "a8");
				rook.pieceFile = PieceFile.valueOf(String.valueOf('d'));
				updateArraylist(rook);
			}
		}
    
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
		ReturnPlay returnPlay = new ReturnPlay();

		if(move.equals("resign")){
			returnPlay.piecesOnBoard = pieces;
			if(cplayer == Player.white){
				returnPlay.message = ReturnPlay.Message.RESIGN_BLACK_WINS;
				return returnPlay;
			}
			returnPlay.message = ReturnPlay.Message.RESIGN_WHITE_WINS;
			return returnPlay;
		}

		String[] moves = move.split(" ");
		String start = moves[0];
		String end = moves[1];
		String promotionPiece = null;
		boolean isDraw = false;

		// Check if this is a draw offer with a move
		if(moves.length > 2 && moves[2].equals("draw?")) {
			isDraw = true;
		} else if(moves.length > 2) {
			promotionPiece = moves[2];
		}
		
		ReturnPiece piece = getPiece(start);
		
		if(piece == null){
			returnPlay.message = (isDraw) ? ReturnPlay.Message.DRAW : ReturnPlay.Message.ILLEGAL_MOVE;
			returnPlay.piecesOnBoard = pieces;
			return returnPlay; 
		}

		boolean isWhite = piece.pieceType.toString().charAt(0) == 'W';

		// Store original position and potential captured piece
		PieceFile originalFile = piece.pieceFile;
		int originalRank = piece.pieceRank;
		ReturnPiece capturedPiece = getPiece(end);

		// Check destination piece
		if(capturedPiece != null) {
			boolean destIsWhite = capturedPiece.pieceType.toString().charAt(0) == 'W';
			if(isWhite == destIsWhite) {
				returnPlay.message = (isDraw) ? ReturnPlay.Message.DRAW :ReturnPlay.Message.ILLEGAL_MOVE;
				returnPlay.piecesOnBoard = pieces;
				return returnPlay;
			}
			// Remove the captured piece before checking for check
			pieces.remove(capturedPiece);
		}

		// Temporarily make the move
		piece.pieceFile = PieceFile.valueOf(String.valueOf(end.charAt(0)));
		piece.pieceRank = end.charAt(1) - '0';

		// Check if this move would leave the king in check
		if(isInCheck(isWhite)) {
			// Restore position and return illegal move
			piece.pieceFile = originalFile;
			piece.pieceRank = originalRank;
			if(capturedPiece != null) {
				pieces.add(capturedPiece);
			}
			returnPlay.message = (isDraw) ? ReturnPlay.Message.DRAW : ReturnPlay.Message.ILLEGAL_MOVE;
			returnPlay.piecesOnBoard = pieces;
			return returnPlay;
		}

		// Restore position to check specific piece rules
		piece.pieceFile = originalFile;
		piece.pieceRank = originalRank;
		if(capturedPiece != null) {
			pieces.add(capturedPiece);
		}

		// Check if current player is in check and move doesn't get out of check
		if(isInCheck(isWhite) && !moveGetsOutOfCheck(piece, move)) {
			returnPlay.message = (isDraw) ? ReturnPlay.Message.DRAW : ReturnPlay.Message.ILLEGAL_MOVE;
			returnPlay.piecesOnBoard = pieces;
			return returnPlay;
		}

		// Check if move creates self-check
		if(moveCreatesCheck(piece, move)) {
			returnPlay.message = (isDraw) ? ReturnPlay.Message.DRAW : ReturnPlay.Message.ILLEGAL_MOVE;
			returnPlay.piecesOnBoard = pieces;
			return returnPlay; 
		}

		if(isLegal(piece,move)){
			if(piece instanceof Pawn p){
				// Check for pawn promotion
				int targetRank = end.charAt(1) - '0';
				if((isWhite && targetRank == 8) || (!isWhite && targetRank == 1)) {
					promotePawn(p, end, promotionPiece);
				} else {
					Pawn.move(p,end);
					updateArraylist(p);
				}
			}
			else if(piece instanceof King k){
				// Check for castling
				if(canCastle(k, move)) {
					King.move(k,end);
					updateArraylist(k);
					doCastling(k, move);
				} else {
					King.move(k,end);
					updateArraylist(k);
				}
			}
			else if(piece instanceof Queen q){
				Queen.move(q,end);
				updateArraylist(q);
			}
			else if(piece instanceof Knight k){
				Knight.move(k,end);
				updateArraylist(k);
			}
			else if(piece instanceof Bishop b){
				Bishop.move(b,end);
				updateArraylist(b);
			}
			else if(piece instanceof Rook r){
				Rook.move(r,end);
				updateArraylist(r);
			}

			// After the move is completed, check for captured pieces at the destination
			if(capturedPiece != null && capturedPiece != piece) {
				pieces.remove(capturedPiece);
			}

			returnPlay.piecesOnBoard = pieces;

			// If this is a draw offer, set draw message regardless of other conditions
			if(isDraw) {
				returnPlay.message = ReturnPlay.Message.DRAW;
				return returnPlay;
			}

			// Check if move puts opponent in checkmate
			boolean opponentIsWhite = !isWhite;
			if(isCheckmate(opponentIsWhite)) {
				returnPlay.message = isWhite ? ReturnPlay.Message.CHECKMATE_WHITE_WINS : ReturnPlay.Message.CHECKMATE_BLACK_WINS;
				return returnPlay;
			}

			// Check if move puts opponent in check
			if(isInCheck(opponentIsWhite)) {
				returnPlay.message = ReturnPlay.Message.CHECK;
			}

			//change turns after move is completed
			if(cplayer == Player.white){
				cplayer = Player.black;
			}else{ 
				cplayer = Player.white;
			}
			return returnPlay;
		}else{
			returnPlay.message = (isDraw) ? ReturnPlay.Message.DRAW : ReturnPlay.Message.ILLEGAL_MOVE;
			returnPlay.piecesOnBoard = pieces;
			return returnPlay;
		}
	}
	
		// Helper method to check if a move gets out of check
		private static boolean moveGetsOutOfCheck(ReturnPiece piece, String move) {
			boolean isWhite = piece.pieceType.toString().charAt(0) == 'W';
			String[] moves = move.split(" ");
			String end = moves[1];

			// Store the piece's original position
			PieceFile originalFile = piece.pieceFile;
			int originalRank = piece.pieceRank;
			ReturnPiece capturedPiece = getPiece(end);

			// Temporarily make the move
			if(capturedPiece != null) {
				pieces.remove(capturedPiece);
			}
			piece.pieceFile = PieceFile.valueOf(String.valueOf(end.charAt(0)));
			piece.pieceRank = end.charAt(1) - '0';

			// Check if the move gets out of check
			boolean getsOutOfCheck = !isInCheck(isWhite);

			// Restore the original position
			piece.pieceFile = originalFile;
			piece.pieceRank = originalRank;
			if(capturedPiece != null) {
				pieces.add(capturedPiece);
			}

			return getsOutOfCheck;
		}
	
	/**
	 * This method should reset the game, and start from scratch.
	 */
	public static void start() {
			// Reset game state variables
			cplayer = Player.white;  // Reset to white's turn
			captured = false;        // Reset captured flag
		
		// clear array of all pieces when reset
		if(!pieces.isEmpty()){
			pieces.clear();
		}

		// create all pieces and add them to the arraylist 

		//add pawns to array
		for(int i = 1; i < 9; i++){
			Pawn wp= new Pawn(PieceType.WP, PieceFile.a, 2);
			Pawn bp= new Pawn(PieceType.BP, PieceFile.a, 7);

			switch(i){
                case 1 -> {
                    wp = new Pawn(PieceType.WP, PieceFile.a, 2);
                    bp = new Pawn(PieceType.BP, PieceFile.a, 7);
                        }
                case 2 -> {
                    wp = new Pawn(PieceType.WP, PieceFile.b, 2);
                    bp = new Pawn(PieceType.BP, PieceFile.b, 7);
                        }
                case 3 -> {
                    wp = new Pawn(PieceType.WP, PieceFile.c, 2);
                    bp = new Pawn(PieceType.BP, PieceFile.c, 7);
                        }
                case 4 -> {
                    wp = new Pawn(PieceType.WP, PieceFile.d, 2);
                    bp = new Pawn(PieceType.BP, PieceFile.d, 7);
                        }
                case 5 -> {
                    wp = new Pawn(PieceType.WP, PieceFile.e, 2);
                    bp = new Pawn(PieceType.BP, PieceFile.e, 7);
                        }
                case 6 -> {
                    wp = new Pawn(PieceType.WP, PieceFile.f, 2);
                    bp = new Pawn(PieceType.BP, PieceFile.f, 7);
                        }
                case 7 -> {
                    wp = new Pawn(PieceType.WP, PieceFile.g, 2);
                    bp = new Pawn(PieceType.BP, PieceFile.g, 7);
                        }
                case 8 -> {
                    wp = new Pawn(PieceType.WP, PieceFile.h, 2);
                    bp = new Pawn(PieceType.BP, PieceFile.h, 7);
                        }
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

	public static ReturnPiece getPiece(String start){
		ReturnPiece piece = null;
		for(int i = 0; i < pieces.size(); i++){
			ReturnPiece p = pieces.get(i);
			int rank = p.pieceRank;
			String file = ""+p.pieceFile;

			String pieceStart = file+rank;

			if(pieceStart.equals(start)){
				piece = p; 
			}
			
		}
		return piece;
	}

	public static void updateArraylist(ReturnPiece piece){
		int i = pieces.indexOf(piece);
		ReturnPiece updated = pieces.get(i);

		updated.pieceFile= piece.pieceFile;
		updated.pieceRank = piece.pieceRank;
		updated.pieceType = piece.pieceType;
		
		
		pieces.set(i,updated);
	}

	public static boolean isLegal(ReturnPiece piece, String move){
			// First check if the move would leave the king in check
			String[] moves = move.split(" ");
			String end = moves[1];
			boolean isWhite = piece.pieceType.toString().charAt(0) == 'W';
			
			// Calculate start and end positions once
			int startRank = moves[0].charAt(1) - '0';
			char startFile = moves[0].charAt(0);
			int endRank = moves[1].charAt(1) - '0';
			char endFile = moves[1].charAt(0);
			
			// Check if move is in bounds for all pieces
			if (endFile < 'a' || endFile > 'h' || endRank < 1 || endRank > 8) {
				return false;
			}

			// Check if player moves the wrong colored piece
			if(isWhite && cplayer == Player.black){
				return false;
			} else if(!isWhite && cplayer == Player.white){
				return false;
			}
			
			// Store original position and potential captured piece
			PieceFile originalFile = piece.pieceFile;
			int originalRank = piece.pieceRank;
			ReturnPiece capturedPiece = getPiece(end);

			// Check destination piece
			if(capturedPiece != null) {
				boolean destIsWhite = capturedPiece.pieceType.toString().charAt(0) == 'W';
				if(isWhite == destIsWhite) {
					return false;
				}
				// Remove the captured piece before checking for check
				pieces.remove(capturedPiece);
			}

			// Temporarily make the move
			piece.pieceFile = PieceFile.valueOf(String.valueOf(end.charAt(0)));
			piece.pieceRank = end.charAt(1) - '0';

			// Check if this move would leave the king in check
			if(isInCheck(isWhite)) {
				// Restore position and return false
				piece.pieceFile = originalFile;
				piece.pieceRank = originalRank;
				if(capturedPiece != null) {
					pieces.add(capturedPiece);
				}
				return false;
			}

			// Restore position to check specific piece rules
			piece.pieceFile = originalFile;
			piece.pieceRank = originalRank;
			if(capturedPiece != null) {
				pieces.add(capturedPiece);
			}

			// Continue with the original piece-specific logic
			if(piece instanceof Pawn){
				Pawn pawn = (Pawn)piece;

				//check move is in the bounds of the board
				if (endFile < 'a' || endFile > 'h' || endRank < 1 || endRank > 8) {
					return false;
				}

				// Calculate rank difference (considering direction)
				int rankDiff = endRank - startRank;
				int fileDiff = Math.abs(endFile - startFile);

				// Check if moving in correct direction
				if ((isWhite && rankDiff <= 0) || (!isWhite && rankDiff >= 0)) {
					return false;
				}

				// Handle diagonal captures
				if (fileDiff == 1) {
					if (Math.abs(rankDiff) != 1) {
						return false;
					}
					// Must capture a piece when moving diagonally
					if (capturedPiece == null) {
						return false;
					}
					// Cannot capture own piece
					boolean capturedIsWhite = capturedPiece.pieceType.toString().charAt(0) == 'W';
					if (isWhite == capturedIsWhite) {
						return false;
					}
					pieces.remove(capturedPiece);
					return true;
				}

				// Handle forward moves
				if (fileDiff != 0) {
					return false;  // Must move straight forward
				}

				// Check move distance
				int maxMove = pawn.moved ? 1 : 2;
				if (Math.abs(rankDiff) > maxMove) {
					return false;
				}

				// Check if path is clear
				if (Math.abs(rankDiff) == 2) {
					// Check intermediate square for two-square move
					int intermediateRank = startRank + (isWhite ? 1 : -1);
					if (getPiece("" + startFile + intermediateRank) != null) {
						return false;
					}
				}

				// Check destination square
				if (getPiece(end) != null) {
					return false;
				}

				return true;
			}
			else if(piece instanceof Queen){
				Queen queen = (Queen)piece;
				//check if player moves the wrong colored piece
				if(isWhite && cplayer == Player.black){return false;}
				else if(!isWhite && cplayer == Player.white){ return false;}

				//check if move is in bounds
				if (endFile < 'a' || endFile > 'h' || endRank < 1 || endRank > 8) {
					return false;
				}

				//check if destination has same color piece
				ReturnPiece destPiece = getPiece(end);
				if(destPiece != null) {
					boolean destIsWhite = (destPiece.pieceType.toString().charAt(0) == 'W');
					if(isWhite == destIsWhite) {
						return false;
					}
				}

				//Queen can move diagonally, horizontally, or vertically
				int rankDiff = Math.abs(endRank - startRank);
				int fileDiff = Math.abs(endFile - startFile);

				//check if move is diagonal, horizontal, or vertical
				if(!(rankDiff == fileDiff || rankDiff == 0 || fileDiff == 0)) {
					return false;
				}

				//check for pieces in the path
				int rankStep = startRank == endRank ? 0 : (endRank - startRank) / Math.abs(endRank - startRank);
				int fileStep = startFile == endFile ? 0 : (endFile - startFile) / Math.abs(endFile - startFile);

				int currentRank = startRank + rankStep;
				char currentFile = (char)(startFile + fileStep);

				while(currentRank != endRank || currentFile != endFile) {
					String position = "" + currentFile + currentRank;
					if(getPiece(position) != null) {
						return false;
					}
					currentRank += rankStep;
					currentFile += fileStep;
				}

				if(destPiece != null) {
					pieces.remove(destPiece);
				}
				return true;
			}
			else if(piece instanceof Knight){
				Knight knight = (Knight)piece;
				//check if player moves the wrong colored piece
				if(isWhite && cplayer == Player.black){return false;}
				else if(!isWhite && cplayer == Player.white){ return false;}

				//check if move is in bounds
				if (endFile < 'a' || endFile > 'h' || endRank < 1 || endRank > 8) {
					return false;
				}

				//check if destination has same color piece
				ReturnPiece destPiece = getPiece(end);
				if(destPiece != null) {
					boolean destIsWhite = (destPiece.pieceType.toString().charAt(0) == 'W');
					if(isWhite == destIsWhite) {
						return false;
					}
				}

				//Knight moves in L-shape: 2 squares in one direction and 1 square perpendicular
				int rankDiff = Math.abs(endRank - startRank);
				int fileDiff = Math.abs(endFile - startFile);

				if (!((rankDiff == 2 && fileDiff == 1) || (rankDiff == 1 && fileDiff == 2))) {
				return false; 
			}

				if(destPiece != null) {
					pieces.remove(destPiece);
				}
				return true;
			}
			else if(piece instanceof Bishop){
				Bishop bishop = (Bishop)piece;
				//check if player moves the wrong colored piece
				if(isWhite && cplayer == Player.black){return false;}
				else if(!isWhite && cplayer == Player.white){ return false;}

				//check if move is in bounds
				if (endFile < 'a' || endFile > 'h' || endRank < 1 || endRank > 8) {
					return false;
				}

				//check if destination has same color piece
				ReturnPiece destPiece = getPiece(end);
				if(destPiece != null) {
					boolean destIsWhite = (destPiece.pieceType.toString().charAt(0) == 'W');
					if(isWhite == destIsWhite) {
						return false;
					}
				}

				//Bishop moves diagonally
				int rankDiff = Math.abs(endRank - startRank);
				int fileDiff = Math.abs(endFile - startFile);

				//check if move is diagonal
				if(rankDiff != fileDiff) {
					return false;
				}

				//check for pieces in the path
				int rankStep = (endRank - startRank) / rankDiff;
				int fileStep = (endFile - startFile) / fileDiff;

				int currentRank = startRank + rankStep;
				char currentFile = (char)(startFile + fileStep);

				while(currentRank != endRank) {
					String position = "" + currentFile + currentRank;
					if(getPiece(position) != null) {
						return false;
					}
					currentRank += rankStep;
					currentFile += fileStep;
				}

				if(destPiece != null) {
					pieces.remove(destPiece);
				}
				return true;
			}
			else if(piece instanceof Rook){
				Rook rook = (Rook)piece;
				//check if player moves the wrong colored piece
				if(isWhite && cplayer == Player.black){return false;}
				else if(!isWhite && cplayer == Player.white){ return false;}

				//check if move is in bounds
				if (endFile < 'a' || endFile > 'h' || endRank < 1 || endRank > 8) {
					return false;
				}

				//check if destination has same color piece
				ReturnPiece destPiece = getPiece(end);
				if(destPiece != null) {
					boolean destIsWhite = (destPiece.pieceType.toString().charAt(0) == 'W');
					if(isWhite == destIsWhite) {
						return false;
					}
				}

				//Rook moves horizontally or vertically
				int rankDiff = Math.abs(endRank - startRank);
				int fileDiff = Math.abs(endFile - startFile);

				//check if move is horizontal or vertical
				if(rankDiff != 0 && fileDiff != 0) {
					return false;
				}

				//check for pieces in the path
				int rankStep = rankDiff == 0 ? 0 : (endRank - startRank) / rankDiff;
				int fileStep = fileDiff == 0 ? 0 : (endFile - startFile) / fileDiff;

				int currentRank = startRank + rankStep;
				char currentFile = (char)(startFile + fileStep);

				while(currentRank != endRank || currentFile != endFile) {
					String position = "" + currentFile + currentRank;
					if(getPiece(position) != null) {
						return false;
					}
					currentRank += rankStep;
					currentFile += fileStep;
				}

				if(destPiece != null) {
					pieces.remove(destPiece);
				}
				return true;
			}
			else if(piece instanceof King){
				King king = (King)piece;
				//check if player moves the wrong colored piece
				if(isWhite && cplayer == Player.black){return false;}
				else if(!isWhite && cplayer == Player.white){ return false;}

				//check if move is in bounds
				if (endFile < 'a' || endFile > 'h' || endRank < 1 || endRank > 8) {
					return false;
				}

				// Check for castling move
				if ((startFile == 'e' && startRank == (isWhite ? 1 : 8)) && 
					(end.equals(isWhite ? "g1" : "g8") || end.equals(isWhite ? "c1" : "c8"))) {
					return canCastle(king, move);
				}

				//check if destination has same color piece
				ReturnPiece destPiece = getPiece(end);
				if(destPiece != null) {
					boolean destIsWhite = (destPiece.pieceType.toString().charAt(0) == 'W');
					if(isWhite == destIsWhite) {
						return false;
					}
				}

				//King moves one square in any direction
				int rankDiff = Math.abs(endRank - startRank);
				int fileDiff = Math.abs(endFile - startFile);

				//check if move is one square in any direction
				if(rankDiff > 1 || fileDiff > 1) {
					return false;
				}

				if(destPiece != null) {
					pieces.remove(destPiece);
				}
				return true;
			}

		return true; 
	}
	
}
