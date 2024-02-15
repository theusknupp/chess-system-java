package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {
	
	private ChessMatch chessMatch;

	public Pawn(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];

		Position p = new Position(0, 0);

		if (getColor() == Color.WHITE) { // Se a cor do peão for branca
			p.setValues(position.getRow() - 1, position.getColumn()); // Uma posição acima
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) { // Se a posição existir e estiver vazia, ele pode se mover para lá
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() - 2, position.getColumn()); // Duas posição acima
			Position p2 = new Position(position.getRow() - 1, position.getColumn()); // Testando se há peça na casa à frente
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) && getBoard().positionExists(p2)
					&& !getBoard().thereIsAPiece(p2) && getMoveCount() == 0) {
				// Se a posição existir e estiver vazia na posição 1 e 2 ele pode se mover para lá
				// Checa também se é oi primeiro movimento /\
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() - 1, position.getColumn() - 1); // Diagonal esquerda
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) { // Se a posição existir e estiver com peça oponente, ele pode se mover para lá
				mat[p.getRow()][p.getColumn()] = true;
			}

			p.setValues(position.getRow() - 1, position.getColumn() + 1); // Diagonal direita
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) { // Se a posição existir e estiver com peça oponente, ele pode se mover para lá
				mat[p.getRow()][p.getColumn()] = true;
			}
			
			// Jogada Especial en passant peças brancas
			if (position.getRow() == 3) {
				Position left = new Position(position.getRow(), position.getColumn() -1);
				if (getBoard().positionExists(left) && isThereOpponentPiece(left) && getBoard().piece(left) == chessMatch.getEnPassantVulnerable()) {
					mat[left.getRow() -1][left.getColumn()] = true;
				}
				Position right = new Position(position.getRow(), position.getColumn() +1);
				if (getBoard().positionExists(right) && isThereOpponentPiece(right) && getBoard().piece(right) == chessMatch.getEnPassantVulnerable()) {
					mat[right.getRow() -1][right.getColumn()] = true;
				}
			}
		}

		else {
			// Movimentos para peça preta

			p.setValues(position.getRow() + 1, position.getColumn()); // Uma posição para baixo
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) { // Se a posição existir e estiver vazia, ele pode se mover para lá
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() + 2, position.getColumn()); // Duas posição para baixo
			Position p2 = new Position(position.getRow() + 1, position.getColumn()); // Testando se há peça na casa à frente
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) && getBoard().positionExists(p2)
					&& !getBoard().thereIsAPiece(p2) && getMoveCount() == 0) {
				// Se a posição existir e estiver vazia na posição 1 e 2 ele pode se mover para lá
				// Checa também se é oi primeiro movimento /\
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() + 1, position.getColumn() - 1); // Diagonal esquerda
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) { // Se a posição existir e estiver com peça oponente, ele pode se mover para lá
				mat[p.getRow()][p.getColumn()] = true;
			}

			p.setValues(position.getRow() + 1, position.getColumn() + 1); // Diagonal direita
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) { // Se a posição existir e estiver com peça oponente, ele pode se mover para lá
				mat[p.getRow()][p.getColumn()] = true;
			}
		
			//Jogada especial en passant peças pretas
			if (position.getRow() == 4) {
				Position left = new Position(position.getRow(), position.getColumn() -1);
				if (getBoard().positionExists(left) && isThereOpponentPiece(left) && getBoard().piece(left) == chessMatch.getEnPassantVulnerable()) {
					mat[left.getRow() +1][left.getColumn()] = true;
				}
				Position right = new Position(position.getRow(), position.getColumn() +1);
				if (getBoard().positionExists(right) && isThereOpponentPiece(right) && getBoard().piece(right) == chessMatch.getEnPassantVulnerable()) {
					mat[right.getRow() +1][right.getColumn()] = true;
				}
			}
		}

		return mat;
	}
	
	@Override
	public String toString() {
		return "P";
	}
}