package chess;

public class Bishop extends ReturnPiece {

    public Bishop(PieceType pieceType, PieceFile pieceFile, int pieceRank) {
        super();
        this.pieceType = pieceType;
        this.pieceFile = pieceFile;
        this.pieceRank = pieceRank;
    }

    public static void move(Bishop b, String end) {
        b.pieceRank = end.charAt(1) - '0';
        b.pieceFile = PieceFile.valueOf(String.valueOf(end.charAt(0)));
    }


}
