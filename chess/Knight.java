package chess;

import java.util.ArrayList;

public class Knight extends Piece {

    public Knight(ReturnPiece.PieceType type, ReturnPiece.PieceFile file, int rank) {
        super(type, file, rank);
    }

    protected boolean isPieceMoveValid(ArrayList<ReturnPiece> pieces, ReturnPiece.PieceFile targetFile, int targetRank) {
        int fileDiff = Math.abs(targetFile.ordinal() - file.ordinal());
        int rankDiff = Math.abs(targetRank - rank);
    
        boolean isValid = false;

        if((rankDiff == 2 && fileDiff ==1) || (rankDiff == 1 && fileDiff == 2)){
            isValid = true;
        }
        
        // Knights can jump over pieces, so we only need to check the target square
        return isValid && canCapture(pieces, targetFile, targetRank);
    }
}
