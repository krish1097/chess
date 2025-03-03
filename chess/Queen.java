package chess;

public class Queen extends ReturnPiece {
    public Queen(PieceType pieceType, PieceFile pieceFile, int pieceRank) {
        super();
        this.pieceType = pieceType;
        this.pieceFile = pieceFile;
        this.pieceRank = pieceRank;
    }

    public static void move(Queen q, String end) {
        q.pieceRank = end.charAt(1) - '0';
        q.pieceFile = PieceFile.valueOf(String.valueOf(end.charAt(0)));
    }
}
