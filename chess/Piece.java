package chess;

import java.util.ArrayList;

public abstract class Piece {
    protected ReturnPiece.PieceType type;
    protected ReturnPiece.PieceFile file;
    protected int rank;
    protected boolean hasMoved;

    public Piece(ReturnPiece.PieceType type, ReturnPiece.PieceFile file, int rank) {
        this.type = type;
        this.file = file;
        this.rank = rank;
        this.hasMoved = false;
    }

    public final boolean isValid(ArrayList<ReturnPiece> boardPieces, ReturnPiece.PieceFile targetFile, int targetRank) {
        if (!isPieceMoveValid(boardPieces, targetFile, targetRank)) {
            return false;
        }
        return !selfKingCheck(boardPieces, targetFile, targetRank);
    }

    protected abstract boolean isPieceMoveValid(ArrayList<ReturnPiece> boardPieces, ReturnPiece.PieceFile targetFile, int targetRank);

    protected boolean selfKingCheck(ArrayList<ReturnPiece> boardPieces, ReturnPiece.PieceFile targetFile, int targetRank) {
        // Copy of the board
        ArrayList<ReturnPiece> tempBoard = new ArrayList<>();
        for (ReturnPiece piece : boardPieces) {
            ReturnPiece newPiece = new ReturnPiece();
            newPiece.pieceType = piece.pieceType;
            newPiece.pieceFile = piece.pieceFile;
            newPiece.pieceRank = piece.pieceRank;
            tempBoard.add(newPiece);
        }

        // Find current piece and the capture piece in the temporary board
        ReturnPiece cPiece = null;
        ReturnPiece capturedPiece = null;
        for (ReturnPiece piece : tempBoard) {
            if (piece.pieceFile == file && piece.pieceRank == rank) {
                cPiece = piece;
            } else if (piece.pieceFile == targetFile && piece.pieceRank == targetRank) {
                capturedPiece = piece;
            }
        }

        // Remove captured piece
        if (capturedPiece != null) {
            tempBoard.remove(capturedPiece);
        }

        // Move our piece
        cPiece.pieceFile = targetFile;
        cPiece.pieceRank = targetRank;

        // Find king
        ReturnPiece king = null;
        boolean isWhite = type.name().startsWith("W");
        for (ReturnPiece piece : tempBoard) {
            if (piece.pieceType == (isWhite ? ReturnPiece.PieceType.WK : ReturnPiece.PieceType.BK)) {
                king = piece;
                break;
            }
        }

        // Check if any opponent piece can attack our king
        for (ReturnPiece piece : tempBoard) {
            if ((isWhite && piece.pieceType.name().startsWith("W")) ||(!isWhite && piece.pieceType.name().startsWith("B"))) {
                continue;
            }
            Piece attackingPiece = null;
            switch (piece.pieceType) {
                case WP, BP -> attackingPiece = new Pawn(piece.pieceType, piece.pieceFile, piece.pieceRank);
                case WN, BN -> attackingPiece = new Knight(piece.pieceType, piece.pieceFile, piece.pieceRank);
                case WB, BB -> attackingPiece = new Bishop(piece.pieceType, piece.pieceFile, piece.pieceRank);
                case WR, BR -> attackingPiece = new Rook(piece.pieceType, piece.pieceFile, piece.pieceRank);
                case WQ, BQ -> attackingPiece = new Queen(piece.pieceType, piece.pieceFile, piece.pieceRank);
                case WK, BK -> attackingPiece = new King(piece.pieceType, piece.pieceFile, piece.pieceRank);
            }

            if (attackingPiece != null) {
                // Check if this piece can attack our king
                if (attackingPiece.isPieceMoveValid(tempBoard, king.pieceFile, king.pieceRank)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean pathClear(ArrayList<ReturnPiece> boardPieces, 
                                ReturnPiece.PieceFile targetFile, int targetRank) {
        // Get the direction of movement
        int fileStep = getFileStep(targetFile);
        int rankStep = getRankStep(targetRank);
        
        // Start from the current position (exclusive) and check each square until target (exclusive)
        ReturnPiece.PieceFile currentFile = file;
        int currentRank = rank;
        
        while (true) {
            // Move one step in the direction
            currentFile = getNextFile(currentFile, fileStep);
            currentRank += rankStep;
            
            // If we've reached the target square, stop checking
            if (currentFile == targetFile && currentRank == targetRank) {
                break;
            }
            
            // If we've gone past the target or off the board, the path is invalid
            if (currentFile == null || currentRank < 1 || currentRank > 8) {
                return false;
            }
            
            // Check if there's a piece at the current position
            for (ReturnPiece piece : boardPieces) {
                if (piece.pieceFile == currentFile && piece.pieceRank == currentRank) {
                    return false; // Path is blocked
                }
            }
        }
        
        return true;
    }

     //Gets the step direction for file movement (-1, 0, or 1)
    protected int getFileStep(ReturnPiece.PieceFile targetFile) {
        if (targetFile == file) return 0;
        return targetFile.ordinal() > file.ordinal() ? 1 : -1;
    }

    
    //Gets the step direction for rank movement (-1, 0, or 1)
    protected int getRankStep(int targetRank) {
        if (targetRank == rank) return 0;
        return targetRank > rank ? 1 : -1;
    }


    protected boolean canCapture(ArrayList<ReturnPiece> pieces, 
                               ReturnPiece.PieceFile targetFile, int targetRank) {
        for (int i = 0; i < pieces.size(); i++) {
            if (pieces.get(i).pieceFile == targetFile && pieces.get(i).pieceRank == targetRank) {
                return isOppPiece(pieces.get(i).pieceType);
            }
        }
        
        return true;
    }

    protected ReturnPiece.PieceFile getNextFile(ReturnPiece.PieceFile current, int step) {
        if (step == 0) return current;
        int nextOrdinal = current.ordinal() + step;
        if (nextOrdinal < 0 || nextOrdinal >= ReturnPiece.PieceFile.values().length) {
            return null;
        }
        return ReturnPiece.PieceFile.values()[nextOrdinal];
    }

    

    protected boolean isOppPiece(ReturnPiece.PieceType otherType) {
        // White pieces start with 'W', Black pieces start with 'B'
        return (type.name().startsWith("W") && otherType.name().startsWith("B")) ||
               (type.name().startsWith("B") && otherType.name().startsWith("W"));
    }

  
    public void moveTo(ReturnPiece.PieceFile newFile, int newRank) {
        this.file = newFile;
        this.rank = newRank;
        this.hasMoved = true;
    }

    public ReturnPiece toReturnPiece() {
        ReturnPiece rp = new ReturnPiece();
        rp.pieceType = type;
        rp.pieceFile = file;
        rp.pieceRank = rank;
        return rp;
    }

    public boolean hasMoved() {
        return hasMoved;
    }
} 