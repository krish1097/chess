package chess;

public class Knight extends ReturnPiece {

    public Knight(PieceType pieceType, PieceFile pieceFile, int pieceRank) {
        super();
        this.pieceType = pieceType;
        this.pieceFile = pieceFile;
        this.pieceRank = pieceRank;
    }

    public static void move(Knight k, String end) {
        k.pieceRank = end.charAt(1) - '0';
        k.pieceFile = PieceFile.valueOf(String.valueOf(end.charAt(0)));
    }
}
