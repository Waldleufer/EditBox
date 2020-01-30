package mw.eclipse.TRC_Overlay.impl.test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Logger;

import javax.annotation.Generated;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.tools.configuration.base.MethodRef;

import mw.eclipse.TRC_Overlay.impl.BoxActualizerHelper;
import mw.eclipse.TRC_Overlay.impl.TRCRequirement;

@Generated(value = "org.junit-tools-1.1.0")
public class BoxActualizerHelperTestactualizeBoxes {
	
	@Generated(value = "org.junit-tools-1.1.0")
	private Logger logger = Logger.getLogger(BoxActualizerHelperTestactualizeBoxes.class.toString());
	
	@Before
	public void setUp() throws Exception {
		
	}

	/**
	 * 
	 * @return one Req with two Boxes: {{0, 20}, {21, 40}}
	 */
	public static LinkedList<TRCRequirement> oneRequirementOneBox() {
		
		int[] box0 = {250, 309};
		
		LinkedList<int[]> positions = new LinkedList<int[]>();
		positions.add(box0);
		
		TRCRequirement testReq = new TRCRequirement("TEST-01", positions);
		
		LinkedList<TRCRequirement> out = new LinkedList<TRCRequirement>();
		out.add(testReq);
		
		return out;
		
	}
	
	/**
	 * 
	 * @return one Req with two Boxes: {{0, 20}, {21, 40}}
	 */
	public static LinkedList<TRCRequirement> oneRequirementTwoBoxesSeparatedByOneSpace() {
		
		int[] box0 = {0, 20};
		int[] box1 = {21, 40};
		
		LinkedList<int[]> positions = new LinkedList<int[]>();
		positions.add(box0);
		positions.add(box1);
		
		TRCRequirement testReq = new TRCRequirement("TEST-01", positions);
		
		LinkedList<TRCRequirement> out = new LinkedList<TRCRequirement>();
		out.add(testReq);
		
		return out;
	}

	@MethodRef(name = "actualizeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void noChange_testActualizeBoxes() throws Exception {
		int positionOfChange = 11;
		int amountOfChange = 0;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		LinkedList<int[]> positions = new LinkedList<int[]>();
		
		// Solution:
		
		int[] box0 = {0, 20};
		int[] box1 = {21, 40};
		positions.add(box0);
		positions.add(box1);
				
		// test 1
		reqs = oneRequirementTwoBoxesSeparatedByOneSpace();
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
		
		// test 2
		reqs = oneRequirementTwoBoxesSeparatedByOneSpace();
		reqs.getFirst().setActive(true);
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
	}

	@MethodRef(name = "actualizeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void newBox_testActualizeBoxes() throws Exception {
		
		Random rand = new Random(2406l);
		
		for(int i = 0; i<50; i++) {
			
			int positionOfChange = rand.nextInt(128);
			int amountOfChange = rand.nextInt(128);
			LinkedList<TRCRequirement> reqs = new LinkedList<TRCRequirement>();
			LinkedList<TRCRequirement> result;
			TRCRequirement testReq = null;
			LinkedList<int[]> positions = new LinkedList<int[]>();
			
			// Solution:
			
			int[] box0 = {positionOfChange, positionOfChange + amountOfChange};
			positions.add(box0);
			
			// Test
			testReq = new TRCRequirement("TEST-02", new LinkedList<int[]>());
			testReq.setActive(true);
			reqs.add(testReq);
			
			result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
			Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
		}
	}

	@MethodRef(name = "actualizeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void deleteBehindSingleBox_testActualizeBoxes() throws Exception {
		int positionOfChange = 309;
		int amountOfChange = -100;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		LinkedList<int[]> positions = new LinkedList<int[]>();
		
		// Solution:
		
		int[] box0 = {250, 309};
		positions.add(box0);
				
		// test 1
		reqs = oneRequirementOneBox();
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
		
		// test 2
		reqs = oneRequirementOneBox();
		reqs.getFirst().setActive(true);
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
	}
	
	@MethodRef(name = "actualizeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void deleteBeforeSingleBox_testActualizeBoxes() throws Exception {
		int positionOfChange = 200;
		int amountOfChange = -50;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		LinkedList<int[]> positions = new LinkedList<int[]>();
		
		// Solution:
		
		int[] box0 = {200, 259};
		positions.add(box0);
				
		// test 1
		reqs = oneRequirementOneBox();
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
		
		// test 2
		reqs = oneRequirementOneBox();
		reqs.getFirst().setActive(true);
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
	}
	
	@MethodRef(name = "actualizeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void deleteLeftEdgeSingleBox_testActualizeBoxes() throws Exception {
		int positionOfChange = 250;
		int amountOfChange = -2;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		LinkedList<int[]> positions = new LinkedList<int[]>();
		
		// Solution:
		
		int[] box0 = {250, 307};
		positions.add(box0);
				
		// test 1
		reqs = oneRequirementOneBox();
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
		
		// test 2
		reqs = oneRequirementOneBox();
		reqs.getFirst().setActive(true);
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
	}
	
	@MethodRef(name = "actualizeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void deleteRightEdgeSingleBox_testActualizeBoxes() throws Exception {
		int positionOfChange = 307;
		int amountOfChange = -2;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		LinkedList<int[]> positions = new LinkedList<int[]>();
		
		// Solution:
		
		int[] box0 = {250, 307};
		positions.add(box0);
				
		// test 1
		reqs = oneRequirementOneBox();
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
		
		// test 2
		reqs = oneRequirementOneBox();
		reqs.getFirst().setActive(true);
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
	}

	@MethodRef(name = "actualizeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void delete_testActualizeBoxes() throws Exception {
		int positionOfChange = 11;
		int amountOfChange = -1;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		LinkedList<int[]> positions = new LinkedList<int[]>();
		
		// Solution:
		
		int[] box0 = {0, 19};
		int[] box1 = {20, 39};
		positions.add(box0);
		positions.add(box1);
				
		// test 1
		reqs = oneRequirementTwoBoxesSeparatedByOneSpace();
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
		
		// test 2
		reqs = oneRequirementTwoBoxesSeparatedByOneSpace();
		reqs.getFirst().setActive(true);
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
	}
	
	@MethodRef(name = "actualizeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void delete2_testActualizeBoxes() throws Exception {
		int positionOfChange = 11;
		int amountOfChange = -4;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		LinkedList<int[]> positions = new LinkedList<int[]>();
		
		// Solution:
		
		int[] box0 = {0, 16};
		int[] box1 = {17, 36};
		positions.add(box0);
		positions.add(box1);
				
		// test 1
		reqs = oneRequirementTwoBoxesSeparatedByOneSpace();
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
		
		// test 2
		reqs = oneRequirementTwoBoxesSeparatedByOneSpace();
		reqs.getFirst().setActive(true);
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
	}
	
	@MethodRef(name = "actualizeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void deleteInside_testActualizeBoxes() throws Exception {
		int positionOfChange = 11;
		int amountOfChange = -4;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		LinkedList<int[]> positions = new LinkedList<int[]>();
		
		// Solution:
		
		int[] box0 = {0, 16};
		int[] box1 = {17, 36};
		positions.add(box0);
		positions.add(box1);
				
		// test 1
		reqs = oneRequirementTwoBoxesSeparatedByOneSpace();
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
		
		// test 2
		reqs = oneRequirementTwoBoxesSeparatedByOneSpace();
		reqs.getFirst().setActive(true);
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
	}
	
	@MethodRef(name = "actualizeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void deleteBehind_testActualizeBoxes() throws Exception {
		int positionOfChange = 60;
		int amountOfChange = -20;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		LinkedList<int[]> positions = new LinkedList<int[]>();
		
		// Solution:
		
		int[] box0 = {0, 20};
		int[] box1 = {21, 40};
		positions.add(box0);
		positions.add(box1);
				
		// test 1
		reqs = oneRequirementTwoBoxesSeparatedByOneSpace();
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
		
		// test 2
		reqs = oneRequirementTwoBoxesSeparatedByOneSpace();
		reqs.getFirst().setActive(true);
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
	}
	
	@MethodRef(name = "actualizeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void merge_testActualizeBoxes() throws Exception {
		int positionOfChange;
		int amountOfChange;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		LinkedList<int[]> positions = new LinkedList<int[]>();
		LinkedList<int[]> positions1 = new LinkedList<int[]>();
		
		// Solution 1, 2:
		
		positionOfChange = 20;
		amountOfChange = -1;
		
		int[] box0 = {0, 39};
		positions.add(box0);		
		
		// test 1
		reqs = oneRequirementTwoBoxesSeparatedByOneSpace();
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
		
		// test 2
		reqs = oneRequirementTwoBoxesSeparatedByOneSpace();
		reqs.getFirst().setActive(true);
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
		
		// Solution 3, 4:
		positionOfChange = 18;
		amountOfChange = -3;
		
		int[] box1 = {0, 37};
		positions1.add(box1);	
		
		// test 3
		reqs = oneRequirementTwoBoxesSeparatedByOneSpace();
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions1.toArray(), result.getFirst().getPositions().toArray());

		// test 4
		reqs = oneRequirementTwoBoxesSeparatedByOneSpace();
		reqs.getFirst().setActive(true);
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions1.toArray(), result.getFirst().getPositions().toArray());

	}
	
	@MethodRef(name = "actualizeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void noMergeInsertOne_testActualizeBoxes() throws Exception {
		int positionOfChange;
		int amountOfChange;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		LinkedList<int[]> positions = new LinkedList<int[]>();
		LinkedList<int[]> positions1 = new LinkedList<int[]>();
		
		// Solution 1, 2:
		
		positionOfChange = 20;
		amountOfChange = 1;
		
		int[] box0a = {0, 20};
		int[] box0b = {0, 21};
		int[] box1 = {22, 41};
		positions.add(box0a);
		positions.add(box1);
		
		positions1.add(box0b);
		positions1.add(box1);
		
		// test 1
		reqs = oneRequirementTwoBoxesSeparatedByOneSpace();
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
		
		// test 2
		reqs = oneRequirementTwoBoxesSeparatedByOneSpace();
		reqs.getFirst().setActive(true);
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions1.toArray(), result.getFirst().getPositions().toArray());

	}
	
	@MethodRef(name = "actualizeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void split_testActualizeBoxes() throws Exception {
		int positionOfChange;
		int amountOfChange;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		
		// Solution 1:
		
		positionOfChange = 10;
		amountOfChange = 1;
		
		int[] box0 = {0, 10};
		int[] box1 = {11, 21};
		int[] box2 = {22, 41};
		
		LinkedList<int[]> positions1 = new LinkedList<int[]>();
		positions1.add(box0);
		positions1.add(box1);
		positions1.add(box2);
		
		// test 1
		reqs = oneRequirementTwoBoxesSeparatedByOneSpace();
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions1.toArray(), result.getFirst().getPositions().toArray());
		
		// ------------------------------------------------ //
		
		// Solution 2:
		
		positionOfChange = 30;
		amountOfChange = 1;

		int[] box3 = {0, 20};
		int[] box4 = {21, 30};
		int[] box5 = {31, 41};
		
		

		LinkedList<int[]> positions2 = new LinkedList<int[]>();
		positions2.add(box3);
		positions2.add(box4);
		positions2.add(box5);

		// test 2
		reqs = oneRequirementTwoBoxesSeparatedByOneSpace();
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions2.toArray(), result.getFirst().getPositions().toArray());
	}
	
	/**
	 * 
	 * @return three Req with identical two Boxes: {{0, 20}, {21, 40}}
	 */
	public static LinkedList<TRCRequirement> threeRequirementsWithIdenticalBoxes() {
		
		int[] box0 = {0, 20};
		int[] box1 = {21, 40};
		int[] box0a = {0, 20};
		int[] box1a = {21, 40};
		int[] box0b = {0, 20};
		int[] box1b = {21, 40};
		
		
		LinkedList<int[]> positions1 = new LinkedList<int[]>();
		LinkedList<int[]> positions2 = new LinkedList<int[]>();
		LinkedList<int[]> positions3 = new LinkedList<int[]>();
		positions1.add(box0);
		positions1.add(box1);
		
		positions2.add(box0a);
		positions2.add(box1a);
		
		positions3.add(box0b);
		positions3.add(box1b);
		
		TRCRequirement testReq01 = new TRCRequirement("TEST-01", positions1);
		TRCRequirement testReq02 = new TRCRequirement("TEST-02", positions2);
		TRCRequirement testReq03 = new TRCRequirement("TEST-03", positions3);
		
		LinkedList<TRCRequirement> out = new LinkedList<TRCRequirement>();
		out.add(testReq01);
		out.add(testReq02);
		out.add(testReq03);
		
		return out;
	}
	
	@MethodRef(name = "actualizeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void partialSplit1_testActualizeBoxes() throws Exception {
		int positionOfChange = 0;
		int amountOfChange = 0;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		
		// Solution 1:
		
		positionOfChange = 10;
		amountOfChange = 1;
		
		int[] box0 = {0, 21}; // Happens if Requirement is active
		int[] box1 = {22, 41}; // Rest has to be moved 
		int[] boxSplitLeft = {0, 10}; // If Requirement is not active split occurs
		int[] boxSplitRight = {11, 21};
		
		LinkedList<int[]> positionsNoSplit = new LinkedList<int[]>();
		positionsNoSplit.add(box0);
		positionsNoSplit.add(box1);
		LinkedList<int[]> positionsSplit = new LinkedList<int[]>();
		positionsSplit.add(boxSplitLeft);
		positionsSplit.add(boxSplitRight);
		positionsSplit.add(box1);
		
		// Test 1
		
		for(int a = 0; a<=1; a++) {
			for(int b = 0; b<=1; b++) {
				for(int c = 0; c<=1; c++) {
					System.out.println("abc = " + a + " " + b + " " + c );
					reqs = threeRequirementsWithIdenticalBoxes();
					reqs.get(0).setActive(a==1);
					reqs.get(1).setActive(b==1);
					reqs.get(2).setActive(c==1);
					
					result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
					
					Assert.assertArrayEquals(a==1 ? positionsNoSplit.toArray() : positionsSplit.toArray(), result.get(0).getPositions().toArray());
					Assert.assertArrayEquals(b==1 ? positionsNoSplit.toArray() : positionsSplit.toArray(), result.get(1).getPositions().toArray());
					Assert.assertArrayEquals(c==1 ? positionsNoSplit.toArray() : positionsSplit.toArray(), result.get(2).getPositions().toArray());
				}
			}
		}
	}
	
	@MethodRef(name = "actualizeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void partialSplit2_testActualizeBoxes() throws Exception {
		int positionOfChange = 0;
		int amountOfChange = 0;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		
		// Solution 1:
		
		positionOfChange = 30;
		amountOfChange = 1;
		
		int[] box0 = {0, 20}; // Happens if Requirement is active
		int[] box1 = {21, 41}; // Rest has to be moved 
		int[] boxSplitLeft = {21, 30}; // If Requirement is not active split occurs
		int[] boxSplitRight = {31, 41};
		
		LinkedList<int[]> positionsNoSplit = new LinkedList<int[]>();
		positionsNoSplit.add(box0);
		positionsNoSplit.add(box1);
		LinkedList<int[]> positionsSplit = new LinkedList<int[]>();
		positionsSplit.add(box0);
		positionsSplit.add(boxSplitLeft);
		positionsSplit.add(boxSplitRight);

		
		// Test 1
		
		for(int a = 0; a<=1; a++) {
			for(int b = 0; b<=1; b++) {
				for(int c = 0; c<=1; c++) {
					System.out.println("abc = " + a + " " + b + " " + c );
					reqs = threeRequirementsWithIdenticalBoxes();
					reqs.get(0).setActive(a==1);
					reqs.get(1).setActive(b==1);
					reqs.get(2).setActive(c==1);
					
					result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
					
					Assert.assertArrayEquals(a==1 ? positionsNoSplit.toArray() : positionsSplit.toArray(), result.get(0).getPositions().toArray());
					Assert.assertArrayEquals(b==1 ? positionsNoSplit.toArray() : positionsSplit.toArray(), result.get(1).getPositions().toArray());
					Assert.assertArrayEquals(c==1 ? positionsNoSplit.toArray() : positionsSplit.toArray(), result.get(2).getPositions().toArray());
				}
			}
		}
	}
	
	@MethodRef(name = "actualizeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void deletionsAfterwards_testActualizeBoxes() throws Exception {
		int positionOfChange;
		int amountOfChange;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		LinkedList<int[]> positions = new LinkedList<int[]>();
		
		// Solution 1:
		
		int[] box0 = {0, 20};
		int[] box1 = {21, 40};
		
		positions.add(box0);
		positions.add(box1);

		// Test 1
		
		for(int i = 40; i <= 100; i++) {
			
			positionOfChange = i;
			amountOfChange = -100;
			
			for(int a = 0; a<=1; a++) {
				for(int b = 0; b<=1; b++) {
					for(int c = 0; c<=1; c++) {
						System.out.println("abc = " + a + " " + b + " " + c );
						reqs = threeRequirementsWithIdenticalBoxes();
						reqs.get(0).setActive(a==1);
						reqs.get(1).setActive(b==1);
						reqs.get(2).setActive(c==1);
						
						result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
						
						Assert.assertArrayEquals(positions.toArray(), result.get(0).getPositions().toArray());
						Assert.assertArrayEquals(positions.toArray(), result.get(1).getPositions().toArray());
						Assert.assertArrayEquals(positions.toArray(), result.get(2).getPositions().toArray());
					}
				}
			}
		}

	}
	
	
	/**
	 * 
	 * @return three Reqs with non Overlapping boxes
	 */
	public static LinkedList<TRCRequirement> threeRequirementsWithNonOverlappingBoxes() {
		
		int[] box0 = {0, 20};
		int[] box1 = {21, 40};
		int[] box2 = {100, 130};
		int[] box3 = {135, 200};
		
		LinkedList<int[]> positions1 = new LinkedList<int[]>();
		LinkedList<int[]> positions2 = new LinkedList<int[]>();
		LinkedList<int[]> positions3 = new LinkedList<int[]>();
		positions1.add(box0);
		positions2.add(box1);
		positions3.add(box2);
		positions1.add(box3);
		
		TRCRequirement testReq01 = new TRCRequirement("TEST-01", positions1);
		TRCRequirement testReq02 = new TRCRequirement("TEST-02", positions2);
		TRCRequirement testReq03 = new TRCRequirement("TEST-03", positions3);
		
		LinkedList<TRCRequirement> out = new LinkedList<TRCRequirement>();
		out.add(testReq01);
		out.add(testReq02);
		out.add(testReq03);
		
		return out;
	}

	@MethodRef(name = "actualizeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void bigDelete_testActualizeBoxes() throws Exception {
		int positionOfChange = 0;
		int amountOfChange = 0;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		
		// Solution 1:
		
		positionOfChange = 5;
		amountOfChange = -135;
		
		int[] box0 = {0, 65};
		
		LinkedList<int[]> positions1 = new LinkedList<int[]>();
		LinkedList<int[]> positions2 = new LinkedList<int[]>();
		LinkedList<int[]> positions3 = new LinkedList<int[]>();
		positions1.add(box0);
		
		// Test 1
		
		System.out.println("Big Deletion of Non Overlapping Boxes");
		
		for(int a = 0; a<=1; a++) {
			for(int b = 0; b<=1; b++) {
				for(int c = 0; c<=1; c++) {
					System.out.println("abc = " + a + " " + b + " " + c );
					reqs = threeRequirementsWithNonOverlappingBoxes();
					reqs.get(0).setActive(a==1);
					reqs.get(1).setActive(b==1);
					reqs.get(2).setActive(c==1);
					
					result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
					
					Assert.assertArrayEquals(positions1.toArray(), result.get(0).getPositions().toArray());
					Assert.assertArrayEquals(positions2.toArray(), result.get(1).getPositions().toArray());
					Assert.assertArrayEquals(positions3.toArray(), result.get(2).getPositions().toArray());
				}
			}
		}
		
		// Solution 2:

		positionOfChange = 40;
		amountOfChange = -100;

		int[] box0b = {0, 40}; // Adjacent
		int[] box2b = {10, 40}; // Overlapp
		int[] box4b = {40, 100}; 
		positions1 = new LinkedList<int[]>();
		positions2 = new LinkedList<int[]>();
		positions3 = new LinkedList<int[]>();
		positions1.add(box0b);
		positions3.add(box2b);
		positions2.add(box4b);
		
		// Test 2
		
		System.out.println("Big Deletion of Overlapping Boxes");

		for(int a = 0; a<=1; a++) {
			for(int b = 0; b<=1; b++) {
				for(int c = 0; c<=1; c++) {
					System.out.println("abc = " + a + " " + b + " " + c );
					reqs = threeRequirementsWithAdjacentAndOverlapping();
					reqs.get(0).setActive(a==1);
					reqs.get(1).setActive(b==1);
					reqs.get(2).setActive(c==1);

					result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);

					Assert.assertArrayEquals(positions1.toArray(), result.get(0).getPositions().toArray());
					Assert.assertArrayEquals(positions2.toArray(), result.get(1).getPositions().toArray());
					Assert.assertArrayEquals(positions3.toArray(), result.get(2).getPositions().toArray());
				}
			}
		}
		
	}
	
	
	@MethodRef(name = "actualizeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void bigDeleteRightEdge_testActualizeBoxes() throws Exception {
		int positionOfChange;
		int amountOfChange;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		
		// Solution 1:
		
		positionOfChange = 5;
		amountOfChange = -135;
		
		int[] box0 = {0, 65};
		
		LinkedList<int[]> positions1 = new LinkedList<int[]>();
		LinkedList<int[]> positions2 = new LinkedList<int[]>();
		LinkedList<int[]> positions3 = new LinkedList<int[]>();
		positions1.add(box0);
		
		// Test 1
		
		System.out.println("Testing right edge");
		
		for(int a = 0; a<=1; a++) {
			for(int b = 0; b<=1; b++) {
				for(int c = 0; c<=1; c++) {
					System.out.println("abc = " + a + " " + b + " " + c );
					reqs = threeRequirementsWithNonOverlappingBoxes();
					reqs.get(0).setActive(a==1);
					reqs.get(1).setActive(b==1);
					reqs.get(2).setActive(c==1);
					
					result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
					
					Assert.assertArrayEquals(positions1.toArray(), result.get(0).getPositions().toArray());
					Assert.assertArrayEquals(positions2.toArray(), result.get(1).getPositions().toArray());
					Assert.assertArrayEquals(positions3.toArray(), result.get(2).getPositions().toArray());
				}
			}
		}
		
		// Solution 2:

		positionOfChange = 40;
		amountOfChange = -100;

		int[] box0b = {0, 40}; // Adjacent
		int[] box2b = {10, 40}; // Overlapp
		int[] box4b = {40, 100}; 
		positions1 = new LinkedList<int[]>();
		positions2 = new LinkedList<int[]>();
		positions3 = new LinkedList<int[]>();
		positions1.add(box0b);
		positions3.add(box2b);
		positions2.add(box4b);
		
		// Test 2
		
		System.out.println("Big Deletion of Overlapping Boxes");

		for(int a = 0; a<=1; a++) {
			for(int b = 0; b<=1; b++) {
				for(int c = 0; c<=1; c++) {
					System.out.println("abc = " + a + " " + b + " " + c );
					reqs = threeRequirementsWithAdjacentAndOverlapping();
					reqs.get(0).setActive(a==1);
					reqs.get(1).setActive(b==1);
					reqs.get(2).setActive(c==1);

					result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);

					Assert.assertArrayEquals(positions1.toArray(), result.get(0).getPositions().toArray());
					Assert.assertArrayEquals(positions2.toArray(), result.get(1).getPositions().toArray());
					Assert.assertArrayEquals(positions3.toArray(), result.get(2).getPositions().toArray());
				}
			}
		}
		
	}
	
	/**
	 * 
	 * @return three Reqs with Overlapping boxes
	 */
	public static LinkedList<TRCRequirement> threeRequirementsWithAdjacentAndOverlapping() {
		
		int[] box1_1 = {0, 50}; // Adjacent
		int[] box1_2 = {60, 99}; 
		int[] box2_1 = {50, 60};
		int[] box2_2 = {100, 200};
		int[] box3 = {10, 130}; // Overlapp
		
		LinkedList<int[]> positions1 = new LinkedList<int[]>();
		LinkedList<int[]> positions2 = new LinkedList<int[]>();
		LinkedList<int[]> positions3 = new LinkedList<int[]>();
		positions1.add(box1_1);
		positions1.add(box1_2);
		positions2.add(box2_1);
		positions2.add(box2_2);
		positions3.add(box3);
		
		TRCRequirement testReq01 = new TRCRequirement("TEST-01", positions1);
		TRCRequirement testReq02 = new TRCRequirement("TEST-02", positions2);
		TRCRequirement testReq03 = new TRCRequirement("TEST-03", positions3);
		
		LinkedList<TRCRequirement> out = new LinkedList<TRCRequirement>();
		out.add(testReq01);
		out.add(testReq02);
		out.add(testReq03);
		
		return out;
	}

	@MethodRef(name = "actualizeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void complex_testActualizeBoxes() throws Exception {
		int positionOfChange = 0;
		int amountOfChange = 0;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		
		// Solution 1:
		
		positionOfChange = 10;
		amountOfChange = 1;
		
		int[] box0 = {0, 10};
		int[] box1 = {11, 21};
		int[] box2 = {22, 41};
		
		LinkedList<int[]> positions1 = new LinkedList<int[]>();
		positions1.add(box0);
		positions1.add(box1);
		positions1.add(box2);
		
		// test 1
		reqs = oneRequirementTwoBoxesSeparatedByOneSpace();
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions1.toArray(), result.getFirst().getPositions().toArray());
		
		// ------------------------------------------------ //
		
		// Solution 2:
		
		positionOfChange = 30;
		amountOfChange = 1;

		int[] box3 = {0, 20};
		int[] box4 = {21, 30};
		int[] box5 = {31, 41};
		
		

		LinkedList<int[]> positions2 = new LinkedList<int[]>();
		positions2.add(box3);
		positions2.add(box4);
		positions2.add(box5);

		// test 2
		reqs = oneRequirementTwoBoxesSeparatedByOneSpace();
		result = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions2.toArray(), result.getFirst().getPositions().toArray());
	}
	
}