package chess;

import chess.ReturnPiece.PieceFile;
import java.util.ArrayList;

public class King extends Piece{
    private boolean hasMoved;

    public King(ReturnPiece.PieceType type, ReturnPiece.PieceFile file, int rank) 
    {
        super(type, file, rank);
    }

    protected boolean isPieceMoveValid(ArrayList<ReturnPiece> pieces, ReturnPiece.PieceFile targetFile, int targetRank) {
        // Calculate the difference in ranks and files
        int rankDiff = Math.abs(targetRank - rank);
        int fileDiff = Math.abs(targetFile.ordinal() - file.ordinal());
        
        // Normal king move: can move one square in any direction
        if (rankDiff <= 1 && fileDiff <= 1) {
            return !isFriendly(pieces, targetFile, targetRank);
        }
        
        // Castling
        if (rankDiff == 0 && fileDiff == 2 && !hasMoved) {
            boolean isKingSide = targetFile.ordinal() > file.ordinal();
            ReturnPiece.PieceFile rookFile = isKingSide ? ReturnPiece.PieceFile.h : ReturnPiece.PieceFile.a;
            
            // Check if rook is in correct position and hasn't moved
            ReturnPiece rook = null;
            for (ReturnPiece piece : pieces) {
                if (piece.pieceFile == rookFile && piece.pieceRank == rank &&
                    ((type == ReturnPiece.PieceType.WK && piece.pieceType == ReturnPiece.PieceType.WR) ||
                     (type == ReturnPiece.PieceType.BK && piece.pieceType == ReturnPiece.PieceType.BR))) {
                    rook = piece;
                    break;
                }
            }
            
            if (rook == null) return false;
            
            // Check if path is clear
            int startFile = Math.min(file.ordinal(), targetFile.ordinal());
            int endFile = Math.max(file.ordinal(), targetFile.ordinal());
            for (int f = startFile + 1; f < endFile; f++) {
                if (squareOccupied(pieces, ReturnPiece.PieceFile.values()[f], rank)) {
                    return false;
                }
            }
            
            // The actual check for squares under attack will be done in Chess.executeCastling
            // since we need access to the pieceMap to properly check for attacks
            return true;
        }
        
        return false;
    }

    private boolean squareOccupied(ArrayList<ReturnPiece> pieces, ReturnPiece.PieceFile file, int rank) {
        for (int i = 0; i < pieces.size(); i++) {
            if (pieces.get(i).pieceFile == file && pieces.get(i).pieceRank == rank) {
                return true;
            }
        }
        return false;
    }

    public static void move(ReturnPiece k, String end) {
        k.pieceRank = end.charAt(1) - '0';
        k.pieceFile = PieceFile.valueOf(String.valueOf(end.charAt(0)));
    }

    private boolean isFriendly(ArrayList<ReturnPiece> pieces, ReturnPiece.PieceFile file, int rank) {
        for (int i = 0; i < pieces.size(); i++) {
            if (pieces.get(i).pieceFile == file && pieces.get(i).pieceRank == rank) {
                return !isOppPiece(pieces.get(i).pieceType);
            }
        }
        return false;
    }

}
