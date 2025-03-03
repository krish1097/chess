package chess;

public class Rook extends ReturnPiece {
    private boolean hasMoved;

    public Rook(PieceType pieceType, PieceFile pieceFile, int pieceRank) {
        super();
        this.pieceType = pieceType;
        this.pieceFile = pieceFile;
        this.pieceRank = pieceRank;
        this.hasMoved = false;
    }

    public static void move(Rook r, String end) {
        r.pieceRank = end.charAt(1) - '0';
        r.pieceFile = PieceFile.valueOf(String.valueOf(end.charAt(0)));
        r.hasMoved = true;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

}
