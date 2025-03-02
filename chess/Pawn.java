package chess;

public class Pawn extends ReturnPiece {

    private boolean moved = false;
    private boolean doubleMoved = false;
    public Pawn(PieceType pieceType, PieceFile pieceFile, int pieceRank){
        super();
        this.pieceType = pieceType;
        this.pieceFile = pieceFile;
        this.pieceRank = pieceRank;
    }

    public static void move(Pawn p, String end){
        p.pieceRank = end.charAt(1) - '0';
        p.pieceFile = PieceFile.valueOf(String.valueOf(end.charAt(0)));

    }
    public boolean isLegal(String move){
        
        return true;
    }
    public boolean isCheck(String move){return false;}


    
}
