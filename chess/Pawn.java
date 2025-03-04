package chess;

import java.util.ArrayList;


public class Pawn extends Piece {
    public boolean moved;
    public boolean justMovedTwo;
    private int moveCount;

    private ReturnPiece.PieceFile lastEnPassantFile = null;
    private int lastEnPassantRank = 0;

    public Pawn(ReturnPiece.PieceType type, ReturnPiece.PieceFile file, int rank) {
        super(type, file, rank);
    }
    

    private boolean isSquareOccupied(ArrayList<ReturnPiece> pieces, 
                                   ReturnPiece.PieceFile file, int rank) {
        for (ReturnPiece piece : pieces) {
            if (piece.pieceFile == file && piece.pieceRank == rank) {
                return true;
            }
        }
        return false;
    }

    private boolean isSquareOccupiedByOpponent(ArrayList<ReturnPiece> pieces, 
                                             ReturnPiece.PieceFile file, int rank) {
        for (ReturnPiece piece : pieces) {
            if (piece.pieceFile == file && piece.pieceRank == rank) {
                return isOppPiece(piece.pieceType);
            }
        }
        return false;
    }

    protected boolean isPieceMoveValid(ArrayList<ReturnPiece> pieces, 
                                        ReturnPiece.PieceFile targetFile, int targetRank) {

        int direction = type.name().startsWith("W") ? 1 : -1;
        

        int rankDiff = targetRank - rank;
        int fileDiff = Math.abs(targetFile.ordinal() - file.ordinal());
        
        // Check if moving in the correct direction
        if ((direction == 1 && rankDiff <= 0) || (direction == -1 && rankDiff >= 0)) {
            return false;
        }

        // Normal one square forward move
        if (fileDiff == 0 && Math.abs(rankDiff) == 1) {
            return !isSquareOccupied(pieces, targetFile, targetRank);
        }

        // Initial two square forward move
        if (!hasMoved && fileDiff == 0 && Math.abs(rankDiff) == 2) {
            // Check both squares in front are empty
            ReturnPiece.PieceFile midFile = targetFile;
            int midRank = rank + direction;
            return !isSquareOccupied(pieces, midFile, midRank) && 
                   !isSquareOccupied(pieces, targetFile, targetRank);
        }

        // Diagonal capture (including en passant)
        if (fileDiff == 1 && Math.abs(rankDiff) == 1) {
            // Regular capture
            if (isSquareOccupiedByOpponent(pieces, targetFile, targetRank)) {
                return true;
            }
            
            // En passant capture
            if (targetFile == lastEnPassantFile && targetRank == lastEnPassantRank) {
                // Find and verify the pawn to be captured
                ReturnPiece capturedPawn = null;
                for (ReturnPiece piece : pieces) {
                    if (piece.pieceFile == targetFile && 
                        piece.pieceRank == (targetRank - direction) &&
                        (piece.pieceType == (direction == 1 ? ReturnPiece.PieceType.BP : ReturnPiece.PieceType.WP))) {
                        capturedPawn = piece;
                        break;
                    }
                }
                return capturedPawn != null;
            }
        }

        return false;
    }

    /*
    public boolean canEnPassant(String targetSquare) {
        // Check if the pawn is in the correct rank for en passant
        boolean isWhite = this.pieceType == PieceType.WP;
        if ((isWhite && this.pieceRank != 5) || (!isWhite && this.pieceRank != 4)) {
            return false;
        }

        // Get the target square coordinates
        int targetRank = targetSquare.charAt(1) - '0';
        char targetFile = targetSquare.charAt(0);

        // Check if the move is diagonal
        if (Math.abs(targetFile - this.pieceFile.toString().charAt(0)) != 1) {
            return false;
        }

        // Check if the target square is in the correct rank
        if ((isWhite && targetRank != 6) || (!isWhite && targetRank != 3)) {
            return false;
        }

        return true;
    }
         */

    public int getMoveCount() {
        return moveCount;
    }

    public boolean hasJustMovedTwo() {
        return justMovedTwo;
    }

    public void setEnPassantTarget(ReturnPiece.PieceFile file, int rank) {
        this.lastEnPassantFile = file;
        this.lastEnPassantRank = rank;
    }

    public boolean isEnPassantCapture(ReturnPiece.PieceFile targetFile, int targetRank) {
        return targetFile == lastEnPassantFile && targetRank == lastEnPassantRank;
    }


}
