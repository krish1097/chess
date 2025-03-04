package chess;

import java.util.ArrayList;

public class Rook extends Piece {
    private boolean hasMoved;

    public Rook(ReturnPiece.PieceType type, ReturnPiece.PieceFile file, int rank) 
    {
        super(type, file, rank);
    }

    @Override
    protected boolean isPieceMoveValid(ArrayList<ReturnPiece> pieces, ReturnPiece.PieceFile targetFile, int targetRank) 
    {
        boolean vertMove = file == targetFile;
        boolean horiMove = rank == targetRank;
        
        if (!vertMove && !horiMove) 
        {
            return false;
        }
        return canCapture(pieces, targetFile, targetRank) && pathClear(pieces, targetFile, targetRank);
    }

    /*
    public static void move(Rook r, String end) {
        r.pieceRank = end.charAt(1) - '0';
        r.pieceFile = PieceFile.valueOf(String.valueOf(end.charAt(0)));
        r.hasMoved = true;
    } */

    public boolean hasMoved() {
        return hasMoved;
    }

}
