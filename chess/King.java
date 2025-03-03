package chess;

public class King extends ReturnPiece{
    private boolean hasMoved;

    public King(PieceType pieceType, PieceFile pieceFile, int pieceRank){
        super();
        this.pieceType = pieceType;
        this.pieceFile = pieceFile;
        this.pieceRank = pieceRank;
        this.hasMoved = false;
    }

    public static void move(King k, String end) {
        k.pieceRank = end.charAt(1) - '0';
        k.pieceFile = PieceFile.valueOf(String.valueOf(end.charAt(0)));
        k.hasMoved = true;
    }

    public boolean hasMoved() {
        return hasMoved;
    }
}
