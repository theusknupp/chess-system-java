package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Pawn;
import chess.pieces.Rook;

public class ChessMatch {

	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	private boolean checkMate;
	
	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();
	
	
	public ChessMatch() {
		board = new Board(8, 8);
		turn =1;
		currentPlayer = Color.WHITE;
		check = false;
		initialSetup();
	}

	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		for (int i = 0; i < board.getRows(); i++) {
			for (int j = 0; j < board.getRows(); j++) {
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}
	
	public int getTurn() {
		return turn;
	}
	
	public Color getCurrentPlayer() {
		return currentPlayer;
	}
	
	public boolean getCheck() {
		return check;
	}
	
	public boolean getCheckMate() {
		return checkMate;
	}
	
	public boolean[][] possibleMoves(ChessPosition sourcePosition) {
		Position position = sourcePosition.toPosition();
		validateSourcePosition(position);
		return board.piece(position).possibleMoves();
		}
	
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		validateSourcePosition(source);
		validateTargetPosition(source, target);
		Piece capturedPiece = makeMove(source, target); //Execução do movimento
		
		if (testCheck(currentPlayer)) { //Checagem para saber se o movimento do jogador esta colocando seu REI em check
			undoMove(source, target, capturedPiece);
			throw new ChessException("You can't put yourself in check");
		}
		
		check = (testCheck(opponent(currentPlayer))) ? true : false; //Verificando se o test check do oponente é verdadeiro ou falso para definir estado da partida
		
		if (testCheckMate(opponent(currentPlayer))) { //Se a jogada deixou o oponente em checkMate, o jogo acabou
			checkMate = true;
		}
		else { //Caso contrario, vai para o próximo turno
			nextTurn();
		}
		
		return (ChessPiece)capturedPiece;
	}
	
	private void validateSourcePosition(Position position) {
		if (!board.thereIsAPiece(position)) { //Exceção de verificação se há peças na devida posição
			throw new ChessException("There is no piece on source position"); 
		}
		if (currentPlayer != ((ChessPiece)board.piece(position)).getColor()) { //Exceção se a peça escolhida é do jogador de posse do turno
			throw new ChessException("The chose piece is not yours");
		}
		if (!board.piece(position).isThereAnyPossibleMove()) { //Exceção de verificação se há movimentos para esta peça
			throw new ChessException("There is no possible moves for the chosen piece");
		}
	}
	
	private void validateTargetPosition(Position source, Position target) {
		if (!board.piece(source).possibleMove(target)) {
			throw new ChessException("The chosen piece, can't move to target position");
		}
	}
	
	private Piece makeMove(Position source, Position target) {
		ChessPiece p = (ChessPiece) board.removePiece(source); //Tirando peça de origem
		p.increaseMoveCount(); //Incrementando 1 a quantidade de movimentos de peças
		Piece capturedPiece = board.removePiece(target); //Tirando do tabuleiro possível peça capturada na posição destino e guardando na variavel
		board.placePiece(p, target); //Colocando na posição de destino a peça que estava na origem
		
		if (capturedPiece != null) { 		//Se a captura de peça for valida
			piecesOnTheBoard.remove(capturedPiece); //Remove a peça capturada da contagem de peças do tabuleiro
			capturedPieces.add(capturedPiece); //Adiciona a peça capturada a contagem de peças capturadas
		}
		
		return capturedPiece;
	}
	
	private void undoMove(Position source, Position target, Piece capturedPiece) { //Desfazendo a jogada
		ChessPiece p = (ChessPiece) board.removePiece(target); //Pega a peça que esta na posição destino
		p.decreaseMoveCount(); //Decrementando 1 a quantidade de movimentos da peça
		board.placePiece(p, source); //Pegando a peça P e colocando na posição de origem novamente
		if (capturedPiece != null) { 
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece); //Remove a peça capturada da contagem de peças capturadas
			piecesOnTheBoard.add(capturedPiece); //Adiciona a peça capturada novamente a contagem de peças de tabuleiro
		}
		
	}
	
	private void nextTurn() { //Troca de turno entre jogadores
		turn++;
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE; 
	}
	
	private Color opponent(Color color) { //Definindo qual a cor do oponente
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private ChessPiece king(Color color) { //Procurando na lista de jogo, qual o rei da determinada cor
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList()); 
		//Recebe as peças em jogo, procura toda peça X, e filtra a peça que é da cor passada como argumento
		//fazendo downcasting Piece de para ChessPiece, pois é ChessPiece que tem cor.
		for (Piece p : list) {
			if (p instanceof King) { //Se a peça P for instancia de Rei
				return (ChessPiece)p; //Retorna o REI
			}
		}
		throw new IllegalStateException("There is no "+color+" King on the board");
		
	}
	
	private boolean testCheck(Color color) { //Teste para saber se o REI esta em checkmate
		Position kingPosition = king(color).getChessPosition().toPosition(); //Pegando a posiçao do REI em formato de matriz
		List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
		//
		for (Piece p : opponentPieces) {
			boolean[][] mat = p.possibleMoves(); //Matriz de movimentos possiveis da peça passada como argumento
			if (mat[kingPosition.getRow()][kingPosition.getColumn()]) { 
				//Se na matriz de movimentos possiveis a posição do REI for encontrada, o rei esta em checkmate 
				return true;
			}
		}
		return false;
	}
	
	private boolean testCheckMate(Color color) { //Teste para saber se existe algum movimento possível para sair do check
		if (!testCheck(color)) {
			return false;
		}
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
	for (Piece p : list) {
		boolean[][] mat = p.possibleMoves();
		for (int i =0; i<board.getRows(); i++) { //Percorrendo linhas
			for (int j=0; j<board.getColumns(); j++) { //Percorrendo colunas
				if (mat[i][j]) { //Testando se o movimento é possível
					Position source = ((ChessPiece)p).getChessPosition().toPosition(); //Posição origem
					Position target = new Position(i, j); //Posição de destino
					Piece capturedPiece = makeMove(source, target); //Movimento de sair da origem e ir para destino
					boolean testCheck = testCheck(color); // testando se ainda está em check
					undoMove(source, target, capturedPiece); //desfazendo o movimento teste
					if (!testCheck) { //Se não estava em check, significa que o movimento realizado tirou de check
						return false;
					}
				}
			}
				
		}
	}
	return true;
	}
	
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}
	
	private void initialSetup() {
		 placeNewPiece('a', 1, new Rook(board, Color.WHITE));
	        placeNewPiece('e', 1, new King(board, Color.WHITE));
	        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
	        placeNewPiece('a', 2, new Pawn(board, Color.WHITE));
	        placeNewPiece('b', 2, new Pawn(board, Color.WHITE));
	        placeNewPiece('c', 2, new Pawn(board, Color.WHITE));
	        placeNewPiece('d', 2, new Pawn(board, Color.WHITE));
	        placeNewPiece('e', 2, new Pawn(board, Color.WHITE));
	        placeNewPiece('f', 2, new Pawn(board, Color.WHITE));
	        placeNewPiece('g', 2, new Pawn(board, Color.WHITE));
	        placeNewPiece('h', 2, new Pawn(board, Color.WHITE));

	        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
	        placeNewPiece('e', 8, new King(board, Color.BLACK));
	        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
	        placeNewPiece('a', 7, new Pawn(board, Color.BLACK));
	        placeNewPiece('b', 7, new Pawn(board, Color.BLACK));
	        placeNewPiece('c', 7, new Pawn(board, Color.BLACK));
	        placeNewPiece('d', 7, new Pawn(board, Color.BLACK));
	        placeNewPiece('e', 7, new Pawn(board, Color.BLACK));
	        placeNewPiece('f', 7, new Pawn(board, Color.BLACK));
	        placeNewPiece('g', 7, new Pawn(board, Color.BLACK));
	        placeNewPiece('h', 7, new Pawn(board, Color.BLACK));
		
		
	}
}