package chess;

public class Pawn extends ReturnPiece {
    public boolean moved;
    public boolean justMovedTwo;
    private int moveCount;

    public Pawn(PieceType pieceType, PieceFile pieceFile, int pieceRank) {
        super();
        this.pieceType = pieceType;
        this.pieceFile = pieceFile;
        this.pieceRank = pieceRank;
        this.moved = false;
        this.justMovedTwo = false;
        this.moveCount = 0;
    }

    public static void move(Pawn p, String end) {
        int oldRank = p.pieceRank;

        p.pieceRank = end.charAt(1) - '0';
        p.pieceFile = PieceFile.valueOf(String.valueOf(end.charAt(0)));
        
        // Check if this is a two-square move
        if (Math.abs(p.pieceRank - oldRank) == 2) {
            p.justMovedTwo = true;
        } else {
            p.justMovedTwo = false;
        }
        
        p.moved = true;
        p.moveCount++;
    }

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

    public int getMoveCount() {
        return moveCount;
    }

    public boolean hasJustMovedTwo() {
        return justMovedTwo;
    }

}
