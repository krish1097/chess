package chess;

import chess.ReturnPiece.PieceFile;
import chess.ReturnPiece.PieceType;

public class Queen extends ReturnPiece{
    public Queen(PieceType pieceType, PieceFile pieceFile, int pieceRank){
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
