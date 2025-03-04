package chess;

import chess.ReturnPiece.PieceFile;
import java.util.ArrayList;

public class Queen extends Piece {

    public Queen(ReturnPiece.PieceType type, ReturnPiece.PieceFile file, int rank) 
    {
        super(type, file, rank);
    }


    public static void move(ReturnPiece q, String end) {
        q.pieceRank = end.charAt(1) - '0';
        q.pieceFile = PieceFile.valueOf(String.valueOf(end.charAt(0)));
    }
    
    protected boolean isPieceMoveValid(ArrayList<ReturnPiece> pieces, ReturnPiece.PieceFile targetFile, int targetRank) 
    {   

        boolean vertMove = file == targetFile;
        boolean horiMove = rank == targetRank;
        int fileDiff = Math.abs(targetFile.ordinal() - file.ordinal());
        int rankDiff = Math.abs(targetRank - rank);
        
        
        boolean DiaMove = rankDiff == fileDiff;
        
        if (!vertMove && !horiMove && !DiaMove) 
        {
            return false;
        }
        return canCapture(pieces, targetFile, targetRank) && pathClear(pieces, targetFile, targetRank);
    }

    
}
