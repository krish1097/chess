package chess;

import java.util.ArrayList;

public class Bishop extends Piece {

    public Bishop(ReturnPiece.PieceType type, ReturnPiece.PieceFile file, int rank) 
    {
        super(type, file, rank);
    }

    protected boolean isPieceMoveValid(ArrayList<ReturnPiece> pieces, ReturnPiece.PieceFile targetFile, int targetRank) 
    {   
        int fileDiff = Math.abs(targetFile.ordinal() - file.ordinal());
        int rankDiff = Math.abs(targetRank - rank);

        // Bishop can only move diagonally
        if (fileDiff != rankDiff) 
        {
            return false;
        }

        return canCapture(pieces, targetFile, targetRank) && pathClear(pieces, targetFile, targetRank);
    }

    /*
    public static void move(Bishop b, String end) {
        b.pieceRank = end.charAt(1) - '0';
        b.pieceFile = PieceFile.valueOf(String.valueOf(end.charAt(0)));
    } */


}
