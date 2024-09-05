package com.github.ryand6.sudokuweb.repositories;

//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//@SpringBootTest
//@ExtendWith(SpringExtension.class)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//public class SudokuPuzzleDaoImplIntegrationTests {

//    private SudokuPuzzleDaoImpl underTest;
//
//    @Autowired
//    public SudokuPuzzleDaoImplIntegrationTests(SudokuPuzzleDaoImpl underTest) {
//        this.underTest = underTest;
//    }
//
//    @Test
//    public void testSudokuPuzzleCreationAndRecall() {
//        SudokuPuzzle sudokuPuzzle = TestDataUtil.createTestSudokuPuzzleA();
//        underTest.create(sudokuPuzzle);
//        Optional<SudokuPuzzle> result = underTest.findOne(sudokuPuzzle.getId());
//        assertThat(result).isPresent();
//        assertThat(result.get()).isEqualTo(sudokuPuzzle);
//    }
//
//    @Test
//    public void testMultipleSudokuPuzzlesCreatedAndRecalled() {
//        SudokuPuzzle puzzleA = TestDataUtil.createTestSudokuPuzzleA();
//        underTest.create(puzzleA);
//        SudokuPuzzle puzzleB = TestDataUtil.createTestSudokuPuzzleB();
//        underTest.create(puzzleB);
//        SudokuPuzzle puzzleC = TestDataUtil.createTestSudokuPuzzleC();
//        underTest.create(puzzleC);
//
//        List<SudokuPuzzle> result = underTest.find();
//        assertThat(result)
//                .hasSize(3)
//                .containsExactly(puzzleA, puzzleB, puzzleC);
//    }
//
//    @Test
//    public void testSudokuPuzzleFullUpdate() {
//        SudokuPuzzle sudokuPuzzleA = TestDataUtil.createTestSudokuPuzzleA();
//        underTest.create(sudokuPuzzleA);
//        sudokuPuzzleA.setDifficulty("medium");
//        underTest.update(sudokuPuzzleA.getId(), sudokuPuzzleA);
//        Optional<SudokuPuzzle> result = underTest.findOne(sudokuPuzzleA.getId());
//        assertThat(result).isPresent();
//        assertThat(result.get()).isEqualTo(sudokuPuzzleA);
//    }
//
//    @Test
//    public void testSudokuPuzzleDeletion() {
//        SudokuPuzzle sudokuPuzzleA = TestDataUtil.createTestSudokuPuzzleA();
//        underTest.create(sudokuPuzzleA);
//        underTest.delete(sudokuPuzzleA.getId());
//        Optional<SudokuPuzzle> result = underTest.findOne(sudokuPuzzleA.getId());
//        assertThat(result).isEmpty();
//    }
//
//}
