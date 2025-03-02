package chess;

public class King extends ReturnPiece{

    public King(PieceType pieceType, PieceFile pieceFile, int pieceRank){
        super();
        this.pieceType = pieceType;
        this.pieceFile = pieceFile;
        this.pieceRank = pieceRank;
    }
    public boolean isLegal(String move){
        return true;
    }
    public boolean isCheck(String move){return false;}

    
}
