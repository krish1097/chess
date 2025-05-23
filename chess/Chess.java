//Darshan Surti dps194
//Krish Patel kap428
package chess;

import chess.ReturnPiece.PieceFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Chess {

    enum Player { white, black }

	private static Player cPlayer;

	private static Map<ReturnPiece, Piece> pObjs;
	private static ArrayList<ReturnPiece> pieces;
    
    
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
        returnPlay.piecesOnBoard = new ArrayList<>(pieces);
        
        // if player resigns
        if (move.equals("resign")) {
			
			if(cPlayer == Player.white){
				returnPlay.message = ReturnPlay.Message.RESIGN_BLACK_WINS;
				return returnPlay;
			}
            returnPlay.message = ReturnPlay.Message.RESIGN_WHITE_WINS;
            return returnPlay;
        }
        
        if (move.trim().isEmpty()) {
            returnPlay.message = ReturnPlay.Message.ILLEGAL_MOVE;
            return returnPlay;
        }

        // check for 2-3 contents
        String[] parts = move.trim().split("\\s+");
        if (parts.length < 2 || parts.length > 3) {
            returnPlay.message = ReturnPlay.Message.ILLEGAL_MOVE;
            return returnPlay;
        }

        // only two characters file and rank passed to cmd
        if (parts[0].length() != 2 || parts[1].length() != 2) {
            returnPlay.message = ReturnPlay.Message.ILLEGAL_MOVE;
            return returnPlay;
        }

		boolean draw= false;
		if(parts.length > 2 && parts[2].equals("draw?")){
			draw = true;
		}
        
        
		// Parse source and destination coordinates
		ReturnPiece.PieceFile startFile = PieceFile.valueOf(String.valueOf(parts[0].charAt(0)));
		ReturnPiece.PieceFile endFile = PieceFile.valueOf(String.valueOf(parts[1].charAt(0)));
		int startRank = Character.getNumericValue(parts[0].charAt(1));
		int endRank = Character.getNumericValue(parts[1].charAt(1));

		// Validate coordinates
		if (startFile == null || endFile == null || startRank < 1 || startRank > 8 || endRank < 1 || endRank > 8) {
			returnPlay.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return returnPlay;
		}

		// Find the piece to move
		ReturnPiece startPiece = getPiece(parts[0]);
		if (startPiece == null) {
			returnPlay.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return returnPlay;
		}
		
		// Ensure the piece belongs to the current player

		Piece piece = pObjs.get(startPiece);
		if(piece == null || !(cPlayer == Player.white && startPiece.pieceType.name().startsWith("W") ||
				cPlayer == Player.black && startPiece.pieceType.name().startsWith("B"))){
			returnPlay.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return returnPlay;
		}

		// Validate the move according to the piece's own rules
		if (!piece.isValid(pieces, endFile, endRank)) {
			returnPlay.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return returnPlay;
		}
		
		// copy array
		ArrayList<ReturnPiece> tempPieces = copyArray(pieces);
		Map<ReturnPiece, Piece> temppObjs = new HashMap<>();
		for (Map.Entry<ReturnPiece, Piece> entry : pObjs.entrySet()) {
			ReturnPiece tempReturnPiece = findPieceInCopy(tempPieces, entry.getKey());
			if (tempReturnPiece != null) {
				temppObjs.put(tempReturnPiece, entry.getValue());
			}
		}
		
		// Locate the piece to move in the simulation and execute the move there.
		ReturnPiece tempstartPiece = findPieceInCopy(tempPieces, startPiece);
		if (!executeMove(tempPieces, temppObjs, tempstartPiece, piece, endFile, endRank, parts)) {
			returnPlay.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return returnPlay;
		}
		
		// Check if the move exposes the player's own king to check
		if (isOwnKingInCheck(tempPieces, temppObjs, cPlayer)) {
			returnPlay.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return returnPlay;
		}
		
		// The move is legal; update the actual board state.
		pieces = tempPieces;
		pObjs = temppObjs;
		
		// Determine if the move gives check or checkmate
		Player opponent = (cPlayer == Player.white) ? Player.black : Player.white;
		boolean isCheck = isKingInCheck(pieces, pObjs, opponent);
		boolean isCheckmate = isCheck && isCheckmate(pieces, pObjs, opponent);
		
		if (draw) {
			returnPlay.message = ReturnPlay.Message.DRAW;
		} else if (isCheckmate) {
			returnPlay.message = cPlayer == Player.white ?
				ReturnPlay.Message.CHECKMATE_WHITE_WINS : ReturnPlay.Message.CHECKMATE_BLACK_WINS;
		} else if (isCheck) {
			returnPlay.message = ReturnPlay.Message.CHECK;
		}
		
		// Switch the turn if the game is not over.
		if (!isCheckmate && !draw) {
			cPlayer = (cPlayer == Player.white) ? Player.black : Player.white;
		}
		
		returnPlay.piecesOnBoard = pieces;
		return returnPlay;
        
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

	private static ArrayList<ReturnPiece> copyArray(ArrayList<ReturnPiece> old) {
        ArrayList<ReturnPiece> newArray = new ArrayList<>();
        for (ReturnPiece piece : old) {
            ReturnPiece newPiece = new ReturnPiece();
            newPiece.pieceType = piece.pieceType;
            newPiece.pieceFile = piece.pieceFile;
            newPiece.pieceRank = piece.pieceRank;
            newArray.add(newPiece);
        }
        return newArray;
    }



	/**
	 * This method should reset the game, and start from scratch.
	 */
	public static void start() {
	
		cPlayer = Player.white;
		pieces = new ArrayList<>();
        pObjs = new HashMap<>();

		// pawns
		for(ReturnPiece.PieceFile F : ReturnPiece.PieceFile.values()){
			Create(ReturnPiece.PieceType.BP , F, 7);
			Create(ReturnPiece.PieceType.WP , F, 2);
		}

		//Kings
		Create(ReturnPiece.PieceType.BK , ReturnPiece.PieceFile.e, 8);
		Create(ReturnPiece.PieceType.WK , ReturnPiece.PieceFile.e, 1);

		//Queens
		Create(ReturnPiece.PieceType.BQ , ReturnPiece.PieceFile.d, 8);
		Create(ReturnPiece.PieceType.WQ , ReturnPiece.PieceFile.d, 1);

		//Bishops
		Create(ReturnPiece.PieceType.BB , ReturnPiece.PieceFile.c, 8);
		Create(ReturnPiece.PieceType.BB , ReturnPiece.PieceFile.f, 8);
		Create(ReturnPiece.PieceType.WB , ReturnPiece.PieceFile.c, 1);
		Create(ReturnPiece.PieceType.WB , ReturnPiece.PieceFile.f, 1);

		//Knights
		Create(ReturnPiece.PieceType.BN , ReturnPiece.PieceFile.b, 8);
		Create(ReturnPiece.PieceType.BN , ReturnPiece.PieceFile.g, 8);
		Create(ReturnPiece.PieceType.WN , ReturnPiece.PieceFile.b, 1);
		Create(ReturnPiece.PieceType.WN , ReturnPiece.PieceFile.g, 1);

		//Rooks
		Create(ReturnPiece.PieceType.BR , ReturnPiece.PieceFile.a, 8);
		Create(ReturnPiece.PieceType.BR , ReturnPiece.PieceFile.h, 8);
		Create(ReturnPiece.PieceType.WR , ReturnPiece.PieceFile.a, 1);
		Create(ReturnPiece.PieceType.WR , ReturnPiece.PieceFile.h, 1);
		
		
	}

	private static void Create(ReturnPiece.PieceType type, ReturnPiece.PieceFile file, int rank) {
        ReturnPiece returnPiece = new ReturnPiece();
        returnPiece.pieceType = type;
        returnPiece.pieceFile = file;
        returnPiece.pieceRank = rank;
        pieces.add(returnPiece);
        
        Piece piece = null;
        switch (type) {
            case WP, BP -> piece = new Pawn(type, file, rank);
            case WR, BR -> piece = new Rook(type, file, rank);
            case WN, BN -> piece = new Knight(type, file, rank);
            case WB, BB -> piece = new Bishop(type, file, rank);
            case WQ, BQ -> piece = new Queen(type, file, rank);
            case WK, BK -> piece = new King(type, file, rank);
            default -> {
                return;
            }
        }
        pObjs.put(returnPiece, piece);
    }

	private static ReturnPiece findPieceInCopy(ArrayList<ReturnPiece> copy, ReturnPiece original) {
        for (ReturnPiece piece : copy) {
            if (piece.pieceFile == original.pieceFile && piece.pieceRank == original.pieceRank && piece.pieceType == original.pieceType) {
                return piece;
            }
        }
        return null;
    }

	private static boolean executeMove(ArrayList<ReturnPiece> boardPieces,
                                       Map<ReturnPiece, Piece> pieceMap,
                                       ReturnPiece sourcePiece,
                                       Piece piece,
                                       ReturnPiece.PieceFile destFile,
                                       int destRank,
                                       String[] moveParts) {


        // Handle castling for king
        if (piece instanceof King && Math.abs(destFile.ordinal() - sourcePiece.pieceFile.ordinal()) == 2) {
            return executeCastling(boardPieces, pieceMap, sourcePiece, piece, destFile);
        }

        // Handle en passant capture	
        if (piece instanceof Pawn && ((Pawn)piece).isEnPassantCapture(destFile, destRank)) {
            int direction = piece.type.name().startsWith("W") ? 1 : -1;
			String endFile = String.valueOf(destFile);
			int endRank = destRank - direction;
			String end = endFile + "" + endRank;
            ReturnPiece capturedPawn = getPiece(end);
            if (capturedPawn != null) {
                boardPieces.remove(capturedPawn);
                pieceMap.remove(capturedPawn);
            }
        } else {
            String endFile = String.valueOf(destFile);
			int endRank = destRank;
			String end = endFile + "" + endRank;
            ReturnPiece capturedPiece = getPiece(end);
            if (capturedPiece != null) {
                boardPieces.remove(capturedPiece);
                pieceMap.remove(capturedPiece);
            }
        }

        // Update en passant targets for all pawns
        if (piece instanceof Pawn && Math.abs(destRank - sourcePiece.pieceRank) == 2) {
            // This pawn just made a two-square move
            int enPassantRank = (sourcePiece.pieceRank + destRank) / 2; // Middle square
            // Update all opponent pawns
            for (Map.Entry<ReturnPiece, Piece> entry : pieceMap.entrySet()) {
                if (entry.getValue() instanceof Pawn &&
                    entry.getKey().pieceType.name().startsWith(
                        sourcePiece.pieceType.name().startsWith("W") ? "B" : "W")) {
                    ((Pawn)entry.getValue()).setEnPassantTarget(destFile, enPassantRank);
                }
            }
        } else {
            // Clear en passant targets for all pawns of the moving side
            for (Map.Entry<ReturnPiece, Piece> entry : pieceMap.entrySet()) {
                if (entry.getValue() instanceof Pawn &&
                    entry.getKey().pieceType.name().startsWith(
                        sourcePiece.pieceType.name().startsWith("W") ? "W" : "B")) {
                    ((Pawn)entry.getValue()).setEnPassantTarget(null, 0);
                }
            }
        }

        // Move the piece
        sourcePiece.pieceFile = destFile;
        sourcePiece.pieceRank = destRank;
        piece.moveTo(destFile, destRank);

        // Handle pawn promotion
        if (piece instanceof Pawn && (destRank == 8 || destRank == 1)) {
            String promotionPiece = moveParts.length > 2 ? moveParts[2] : "Q";
            return handlePawnPromotion(pieceMap, sourcePiece, promotionPiece);
        }

		

        return true;
    }

	private static boolean executeCastling(ArrayList<ReturnPiece> boardPieces,
                                             Map<ReturnPiece, Piece> pieceMap,
                                             ReturnPiece kingPiece,
                                             Piece king,
                                             ReturnPiece.PieceFile targetFile) {
        // Determine castling direction and setup positions
        int castlingDirection = targetFile.ordinal() > kingPiece.pieceFile.ordinal() ? 1 : -1;
        boolean isKingSideCastle = castlingDirection > 0;
        
        // Define rook positions
        ReturnPiece.PieceFile rookStartPos = isKingSideCastle ? ReturnPiece.PieceFile.h : ReturnPiece.PieceFile.a;
        ReturnPiece.PieceFile rookEndPos = isKingSideCastle ? ReturnPiece.PieceFile.f : ReturnPiece.PieceFile.d;
        
        // Locate the rook for castling
        String rookLocation = rookStartPos.toString() + kingPiece.pieceRank;
        ReturnPiece rookPiece = getPiece(rookLocation);
        
        // Validate rook presence and type
        if (!isValidRook(rookPiece, pieceMap)) {
            return false;
        }
        
        Piece rook = pieceMap.get(rookPiece);
        
        // Determine the player's color
        Player currentPlayer = kingPiece.pieceType.name().startsWith("W") ? Player.white : Player.black;
        
        // Check if path is safe for castling
        if (!isCastlingPathSafe(boardPieces, pieceMap, kingPiece, currentPlayer, castlingDirection, targetFile)) {
            return false;
        }
        
        // Execute the castling move
        performCastlingMove(king, kingPiece, rook, rookPiece, targetFile, rookEndPos);
        
        return true;
    }

    private static boolean isValidRook(ReturnPiece rookPiece, Map<ReturnPiece, Piece> pieceMap) {
        if (rookPiece == null) {
            return false;
        }
        Piece rook = pieceMap.get(rookPiece);
        return rook != null && (rook instanceof Rook);
    }

    private static boolean isCastlingPathSafe(ArrayList<ReturnPiece> boardPieces,
                                            Map<ReturnPiece, Piece> pieceMap,
                                            ReturnPiece kingPiece,
                                            Player currentPlayer,
                                            int direction,
                                            ReturnPiece.PieceFile targetFile) {
        // Check if starting position is safe
        if (isSquareUnderAttack(boardPieces, pieceMap, kingPiece.pieceFile, kingPiece.pieceRank, currentPlayer)) {
            return false;
        }
        
        // Check if passing square is safe
        ReturnPiece.PieceFile midFile = ReturnPiece.PieceFile.values()[kingPiece.pieceFile.ordinal() + direction];
        if (isSquareUnderAttack(boardPieces, pieceMap, midFile, kingPiece.pieceRank, currentPlayer)) {
            return false;
        }
        
        // Check if destination is safe
        return !isSquareUnderAttack(boardPieces, pieceMap, targetFile, kingPiece.pieceRank, currentPlayer);
    }

    private static void performCastlingMove(Piece king,
                                          ReturnPiece kingPiece,
                                          Piece rook,
                                          ReturnPiece rookPiece,
                                          ReturnPiece.PieceFile kingTarget,
                                          ReturnPiece.PieceFile rookTarget) {
        // Move king
        king.moveTo(kingTarget, kingPiece.pieceRank);
        kingPiece.pieceFile = kingTarget;
        
        // Move rook
        rook.moveTo(rookTarget, kingPiece.pieceRank);
        rookPiece.pieceFile = rookTarget;
    }

	private static boolean isSquareUnderAttack(ArrayList<ReturnPiece> boardPieces,
                                             Map<ReturnPiece, Piece> pieceMap,
                                             ReturnPiece.PieceFile file,
                                             int rank,
                                             Player defendingPlayer) {
        // Check if any opponent piece can move to this square
        for (ReturnPiece piece : boardPieces) {
            // Skip pieces of the defending player
            if ((defendingPlayer == Player.white && piece.pieceType.name().startsWith("W")) ||
                (defendingPlayer == Player.black && piece.pieceType.name().startsWith("B"))) {
                continue;
            }
            
            Piece attackingPiece = pieceMap.get(piece);
            if (attackingPiece != null && attackingPiece.isValid(boardPieces, file, rank)) {
                return true;
            }
        }
        return false;
    }

	private static boolean handlePawnPromotion(Map<ReturnPiece, Piece> pieceMap,
                                               ReturnPiece pawnPiece,
                                               String promotionPiece) {
        if (promotionPiece == null || promotionPiece.trim().isEmpty()) {
            promotionPiece = "Q"; // Default to Queen if no piece specified
        }

        String piece = promotionPiece.trim().toUpperCase();
        boolean isWhite = pawnPiece.pieceType.name().startsWith("W");
        ReturnPiece.PieceType newType;
        Piece newPiece;

        // Default to queen if not specified or invalid
        switch (promotionPiece.toUpperCase()) {
            case "N":
                newType = isWhite ? ReturnPiece.PieceType.WN : ReturnPiece.PieceType.BN;
                newPiece = new Knight(newType, pawnPiece.pieceFile, pawnPiece.pieceRank);
                break;
            case "B":
                newType = isWhite ? ReturnPiece.PieceType.WB : ReturnPiece.PieceType.BB;
                newPiece = new Bishop(newType, pawnPiece.pieceFile, pawnPiece.pieceRank);
                break;
            case "R":
                newType = isWhite ? ReturnPiece.PieceType.WR : ReturnPiece.PieceType.BR;
                newPiece = new Rook(newType, pawnPiece.pieceFile, pawnPiece.pieceRank);
                break;
            case "Q":
            default:
                newType = isWhite ? ReturnPiece.PieceType.WQ : ReturnPiece.PieceType.BQ;
                newPiece = new Queen(newType, pawnPiece.pieceFile, pawnPiece.pieceRank);
        }

        pawnPiece.pieceType = newType;
        pieceMap.put(pawnPiece, newPiece);
        return true;
    }

	private static boolean isOwnKingInCheck(ArrayList<ReturnPiece> boardPieces, Map<ReturnPiece, Piece> map, Player player) {
        return isKingInCheck(boardPieces, map, player);
    }
    
    private static ReturnPiece findKing(ArrayList<ReturnPiece> boardPieces, Player player) {
        ReturnPiece.PieceType kingType = (player == Player.white) ?
                ReturnPiece.PieceType.WK : ReturnPiece.PieceType.BK;
        for (ReturnPiece piece : boardPieces) {
            if (piece.pieceType == kingType) {
                return piece;
            }
        }
        return null;
    }

	private static boolean isKingInCheck(ArrayList<ReturnPiece> boardPieces, Map<ReturnPiece, Piece> map, Player player) {
        ReturnPiece king = findKing(boardPieces, player);
        if (king == null) return false;
        
        for (ReturnPiece piece : boardPieces) {
            if ((player == Player.white && piece.pieceType.name().startsWith("B")) ||
                (player == Player.black && piece.pieceType.name().startsWith("W"))) {
                Piece attackingPiece = map.get(piece);
                if (attackingPiece != null &&
                    attackingPiece.isValid(boardPieces, king.pieceFile, king.pieceRank)) {
                    return true;
                }
            }
        }
        return false;
    }

	private static boolean isCheckmate(ArrayList<ReturnPiece> boardPieces, Map<ReturnPiece, Piece> map, Player player) {
        // Get all pieces for the current player
        List<ReturnPiece> currentPlayerPieces = getAllPlayerPieces(boardPieces, player);
        
        // Try each possible move for each piece
        for (ReturnPiece currentPiece : currentPlayerPieces) {
            if (!canPieceSaveKing(currentPiece, boardPieces, map, player)) {
                continue;
            }
            return false; // Found at least one legal move that prevents checkmate
        }
        
        // No legal moves found that prevent checkmate
        return true;
    }

    private static List<ReturnPiece> getAllPlayerPieces(ArrayList<ReturnPiece> boardPieces, Player player) {
        String colorPrefix = (player == Player.white) ? "W" : "B";
        return boardPieces.stream()
                .filter(piece -> piece.pieceType.name().startsWith(colorPrefix))
                .collect(Collectors.toList());
    }

    private static boolean canPieceSaveKing(ReturnPiece currentPiece, 
                                          ArrayList<ReturnPiece> boardPieces,
                                          Map<ReturnPiece, Piece> map,
                                          Player player) {
        Piece activePiece = map.get(currentPiece);
        if (activePiece == null) {
            return false;
        }

        // Check all possible squares on the board
        for (ReturnPiece.PieceFile targetFile : ReturnPiece.PieceFile.values()) {
            if (tryMovesInRank(currentPiece, activePiece, targetFile, boardPieces, map, player)) {
                return true;
            }
        }
        
        return false;
    }

    private static boolean tryMovesInRank(ReturnPiece currentPiece,
                                        Piece activePiece,
                                        ReturnPiece.PieceFile targetFile,
                                        ArrayList<ReturnPiece> boardPieces,
                                        Map<ReturnPiece, Piece> map,
                                        Player player) {
        // Try each rank for the current file
        for (int targetRank = 1; targetRank <= 8; targetRank++) {
            if (!activePiece.isValid(boardPieces, targetFile, targetRank)) {
                continue;
            }

            // Create temporary board state
            ArrayList<ReturnPiece> simulatedBoard = copyArray(boardPieces);
            Map<ReturnPiece, Piece> simulatedMap = new HashMap<>(map);
            ReturnPiece simulatedPiece = findPiece(simulatedBoard, currentPiece.pieceFile, currentPiece.pieceRank);
            
            if (simulatedPiece == null) {
                continue;
            }

            // Try the move and check if it saves the king
            if (executeMove(simulatedBoard, simulatedMap, simulatedPiece, activePiece, targetFile, targetRank, new String[0])) {
                if (!isKingInCheck(simulatedBoard, simulatedMap, player)) {
                    return true; // Found a legal move that prevents checkmate
                }
            }
        }
        return false;
    }

	private static ReturnPiece findPiece(ArrayList<ReturnPiece> boardPieces,
                                         ReturnPiece.PieceFile file, int rank) {
        for (ReturnPiece piece : boardPieces) {
            if (piece.pieceFile == file && piece.pieceRank == rank) {
                return piece;
            }
        }
        return null;
    }
    
}
