package mw.eclipse.TRC_Overlay.impl.test;

import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Logger;

import javax.annotation.Generated;

import org.junit.Assert;
import org.junit.Test;
import org.junit.tools.configuration.base.MethodRef;

import mw.eclipse.TRC_Overlay.impl.test.BoxActualizerHelperTestactualizeBoxes;

import mw.eclipse.TRC_Overlay.impl.BoxActualizerHelper;
import mw.eclipse.TRC_Overlay.impl.TRCRequirement;

@Generated(value = "org.junit-tools-1.1.0")
public class BoxActualizerHelperTestchangeBoxes {

	@Generated(value = "org.junit-tools-1.1.0")
	private Logger logger = Logger.getLogger(BoxActualizerHelperTestchangeBoxes.class.toString());

	@MethodRef(name = "changeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void noChange_testChangeBoxes() throws Exception {
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
		reqs = BoxActualizerHelperTestactualizeBoxes.oneRequirementTwoBoxesSeparatedByOneSpace();
		result = BoxActualizerHelper.changeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
		
		// test 2
		reqs = BoxActualizerHelperTestactualizeBoxes.oneRequirementTwoBoxesSeparatedByOneSpace();
		reqs.getFirst().setActive(true);
		result = BoxActualizerHelper.changeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
	}
	
	@MethodRef(name = "changeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void noChange2_testChangeBoxes() throws Exception {
		int positionOfChange = 50;
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
		reqs = BoxActualizerHelperTestactualizeBoxes.oneRequirementTwoBoxesSeparatedByOneSpace();
		result = BoxActualizerHelper.changeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
		
		// test 2
		reqs = BoxActualizerHelperTestactualizeBoxes.oneRequirementTwoBoxesSeparatedByOneSpace();
		reqs.getFirst().setActive(true);
		result = BoxActualizerHelper.changeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
	}
	
	@MethodRef(name = "changeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void newBox_testChangeBoxes() throws Exception {
		
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
			
			result = BoxActualizerHelper.changeBoxes(positionOfChange, amountOfChange, reqs);
			Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
		}
	}
	
	@MethodRef(name = "changeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void setLeftEdgeSingleBox_testChangeBoxes() throws Exception {
		int positionOfChange = 250;
		int amountOfChange = 2;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		LinkedList<int[]> positions = new LinkedList<int[]>();
		LinkedList<int[]> positions1 = new LinkedList<int[]>();
		
		// Solution:
		
		int[] box0a = {250, 309};
		int[] box0b = {252, 309};
		positions.add(box0a);
		positions1.add(box0b);
				
		// test 1
		reqs = BoxActualizerHelperTestactualizeBoxes.oneRequirementOneBox();
		result = BoxActualizerHelper.changeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions1.toArray(), result.getFirst().getPositions().toArray());
		
		// test 2
		reqs = BoxActualizerHelperTestactualizeBoxes.oneRequirementOneBox();
		reqs.getFirst().setActive(true);
		result = BoxActualizerHelper.changeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
	}
	
	@MethodRef(name = "changeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void setLeftEdgeSingleBox2_testChangeBoxes() throws Exception {
		int positionOfChange = 248;
		int amountOfChange = 4;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		LinkedList<int[]> positions = new LinkedList<int[]>();
		LinkedList<int[]> positions1 = new LinkedList<int[]>();
		
		// Solution:
		
		int[] box0a = {248, 309};
		int[] box0b = {252, 309};
		positions.add(box0a);
		positions1.add(box0b);
				
		// test 1
		reqs = BoxActualizerHelperTestactualizeBoxes.oneRequirementOneBox();
		result = BoxActualizerHelper.changeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions1.toArray(), result.getFirst().getPositions().toArray());
		
		// test 2
		reqs = BoxActualizerHelperTestactualizeBoxes.oneRequirementOneBox();
		reqs.getFirst().setActive(true);
		result = BoxActualizerHelper.changeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
	}
	
	@MethodRef(name = "changeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void setRightEdgeSingleBox_testChangeBoxes() throws Exception {
		int positionOfChange = 307;
		int amountOfChange = 2;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		LinkedList<int[]> positions = new LinkedList<int[]>();
		LinkedList<int[]> positions1 = new LinkedList<int[]>();
		
		// Solution:
		
		int[] box0a = {250, 309};
		int[] box0b = {250, 307};
		positions.add(box0a);
		positions1.add(box0b);
				
		// test 1
		reqs = BoxActualizerHelperTestactualizeBoxes.oneRequirementOneBox();
		result = BoxActualizerHelper.changeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions1.toArray(), result.getFirst().getPositions().toArray());
		
		// test 2
		reqs = BoxActualizerHelperTestactualizeBoxes.oneRequirementOneBox();
		reqs.getFirst().setActive(true);
		result = BoxActualizerHelper.changeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
	}
	
	@MethodRef(name = "changeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void splitSingleBox_testChangeBoxes() throws Exception {
		int positionOfChange = 260;
		int amountOfChange = 1;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		LinkedList<int[]> positionsActive = new LinkedList<int[]>();
		LinkedList<int[]> positionsNotActive = new LinkedList<int[]>();
		
		// Solution:
		
		int[] box0 = {250, 309};
		int[] box1left = {250, 260};
		int[] box1rigth = {261, 309};
		positionsActive.add(box0);
		positionsNotActive.add(box1left);
		positionsNotActive.add(box1rigth);
				
		// test 1
		reqs = BoxActualizerHelperTestactualizeBoxes.oneRequirementOneBox();
		reqs.getFirst().setActive(true);
		result = BoxActualizerHelper.changeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positionsActive.toArray(), result.getFirst().getPositions().toArray());
		
		// test 2
		reqs = BoxActualizerHelperTestactualizeBoxes.oneRequirementOneBox();
		result = BoxActualizerHelper.changeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positionsNotActive.toArray(), result.getFirst().getPositions().toArray());
	}
	
	@MethodRef(name = "changeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void mergeTwoBoxes_testChangeBoxes() throws Exception {
		int positionOfChange = 20;
		int amountOfChange = 1;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		LinkedList<int[]> positions = new LinkedList<int[]>();
		LinkedList<int[]> positionsActive = new LinkedList<int[]>();
		
		// Solution:
		
		int[] box0 = {0, 20};
		int[] box1 = {21, 40};
		int[] boxActive = {0, 40};
		positions.add(box0);
		positions.add(box1);
		positionsActive.add(boxActive);
				
		// test 1
		reqs = BoxActualizerHelperTestactualizeBoxes.oneRequirementTwoBoxesSeparatedByOneSpace();
		result = BoxActualizerHelper.changeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
		
		// test 2
		reqs = BoxActualizerHelperTestactualizeBoxes.oneRequirementTwoBoxesSeparatedByOneSpace();
		reqs.getFirst().setActive(true);
		result = BoxActualizerHelper.changeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positionsActive.toArray(), result.getFirst().getPositions().toArray());
	}
	
	@MethodRef(name = "changeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void mergeTwoBoxesLeft_testChangeBoxes() throws Exception {
		int positionOfChange = 19;
		int amountOfChange = 2;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		LinkedList<int[]> positions = new LinkedList<int[]>();
		LinkedList<int[]> positionsActive = new LinkedList<int[]>();
		
		// Solution:
		
		int[] box0 = {0, 19};
		int[] box1 = {21, 40};
		int[] boxActive = {0, 40};
		positions.add(box0);
		positions.add(box1);
		positionsActive.add(boxActive);
				
		// test 1
		reqs = BoxActualizerHelperTestactualizeBoxes.oneRequirementTwoBoxesSeparatedByOneSpace();
		result = BoxActualizerHelper.changeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
		
		// test 2
		reqs = BoxActualizerHelperTestactualizeBoxes.oneRequirementTwoBoxesSeparatedByOneSpace();
		reqs.getFirst().setActive(true);
		result = BoxActualizerHelper.changeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positionsActive.toArray(), result.getFirst().getPositions().toArray());
	}
	
	@MethodRef(name = "changeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void mergeTwoBoxesRight_testChangeBoxes() throws Exception {
		int positionOfChange = 20;
		int amountOfChange = 2;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		LinkedList<int[]> positions = new LinkedList<int[]>();
		LinkedList<int[]> positionsActive = new LinkedList<int[]>();
		
		// Solution:
		
		int[] box0 = {0, 20};
		int[] box1 = {22, 40};
		int[] boxActive = {0, 40};
		positions.add(box0);
		positions.add(box1);
		positionsActive.add(boxActive);
				
		// test 2
		reqs = BoxActualizerHelperTestactualizeBoxes.oneRequirementTwoBoxesSeparatedByOneSpace();
		reqs.getFirst().setActive(true);
		result = BoxActualizerHelper.changeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positionsActive.toArray(), result.getFirst().getPositions().toArray());
		
		// test 1
		reqs = BoxActualizerHelperTestactualizeBoxes.oneRequirementTwoBoxesSeparatedByOneSpace();
		result = BoxActualizerHelper.changeBoxes(positionOfChange, amountOfChange, reqs);
		Assert.assertArrayEquals(positions.toArray(), result.getFirst().getPositions().toArray());
		
	}
	
	@MethodRef(name = "changeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void bigSet_testChangeBoxes() throws Exception {
		int positionOfChange = 0;
		int amountOfChange = 0;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		
		// Solution 1:
		
		positionOfChange = 5;
		amountOfChange = 135;
		
		int[] box1_1active = {0, 200};
		int[] box1_1notActive = {0, 5};
		int[] box1_2notActive = {135, 200};
		
		int[] box2active = {5, 140};
//		int[] box2notActive = null;
		
		int[] box3active = {5, 140};
//		int[] box3notActive = null;
		
		LinkedList<int[]> positions1active 	= new LinkedList<int[]>();
		LinkedList<int[]> positions1 		= new LinkedList<int[]>();
		LinkedList<int[]> positions2active	= new LinkedList<int[]>();
		LinkedList<int[]> positions2 		= new LinkedList<int[]>();
		LinkedList<int[]> positions3active 	= new LinkedList<int[]>();
		LinkedList<int[]> positions3 		= new LinkedList<int[]>();
		
		positions1active.add(box1_1active);
		positions1.add(box1_1notActive);
		positions1.add(box1_2notActive);
		
		positions2active.add(box2active);
		
		positions3active.add(box3active);
		
		// Test 1
		
		System.out.println("Big Set of Non Overlapping Boxes");
		
		for(int a = 0; a<=1; a++) {
			for(int b = 0; b<=1; b++) {
				for(int c = 0; c<=1; c++) {
					System.out.println("abc = " + a + " " + b + " " + c );
					reqs = BoxActualizerHelperTestactualizeBoxes.threeRequirementsWithNonOverlappingBoxes();
					reqs.get(0).setActive(a==1);
					reqs.get(1).setActive(b==1);
					reqs.get(2).setActive(c==1);
					
					result = BoxActualizerHelper.changeBoxes(positionOfChange, amountOfChange, reqs);
					
					Assert.assertArrayEquals((a==1 ? positions1active : positions1).toArray(), result.get(0).getPositions().toArray());
					Assert.assertArrayEquals((b==1 ? positions2active : positions2).toArray(), result.get(1).getPositions().toArray());
					Assert.assertArrayEquals((c==1 ? positions3active : positions3).toArray(), result.get(2).getPositions().toArray());
				}
			}
		}		
	}
	
	@MethodRef(name = "changeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void bigSet2_testChangeBoxes() throws Exception {
		int positionOfChange;
		int amountOfChange;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		
		// Solution 1:
		
		positionOfChange = 51;
		amountOfChange = 5;
		
		LinkedList<int[]> positions1active 	= new LinkedList<int[]>();
		LinkedList<int[]> positions1 		= new LinkedList<int[]>();
		LinkedList<int[]> positions2active	= new LinkedList<int[]>();
		LinkedList<int[]> positions2 		= new LinkedList<int[]>();
		LinkedList<int[]> positions3active 	= new LinkedList<int[]>();
		LinkedList<int[]> positions3 		= new LinkedList<int[]>();
		
		// Req1
		
		int[] box1_1 = {0, 50}; // Adjacent
		int[] box1_3active = {51, 56}; 
		int[] box1_2 = {60, 99}; 
		
		positions1active.add(box1_1);
		positions1active.add(box1_3active);
		positions1active.add(box1_2);
		
		positions1.add(box1_1);
		positions1.add(box1_2);
		
		// Req2
		
		int[] box2_1active = {50, 60};
		int[] box2_1left = {50, 51};
		int[] box2_1right = {56, 60};
		int[] box2_2 = {100, 200};
		
		positions2active.add(box2_1active);
		positions2active.add(box2_2);
		
		positions2.add(box2_1left);
		positions2.add(box2_1right);
		positions2.add(box2_2);
		
		// Req3
		
		int[] box3active = {10, 130}; // Overlapping Box
		int[] box3left = {10, 51}; // Overlapping Box
		int[] box3right = {56, 130}; // Overlapping Box
		
		positions3active.add(box3active);
		
		positions3.add(box3left);
		positions3.add(box3right);
		
		// Test 1
		
		System.out.println("Big Set of Overlapping Boxes");
		
		for(int a = 0; a<=1; a++) {
			for(int b = 0; b<=1; b++) {
				for(int c = 0; c<=1; c++) {
					System.out.println("abc = " + a + " " + b + " " + c );
					reqs = BoxActualizerHelperTestactualizeBoxes.threeRequirementsWithAdjacentAndOverlapping();
					reqs.get(0).setActive(a==1);
					reqs.get(1).setActive(b==1);
					reqs.get(2).setActive(c==1);
					
					result = BoxActualizerHelper.changeBoxes(positionOfChange, amountOfChange, reqs);
					
					Assert.assertArrayEquals((a==1 ? positions1active : positions1).toArray(), result.get(0).getPositions().toArray());
					Assert.assertArrayEquals((b==1 ? positions2active : positions2).toArray(), result.get(1).getPositions().toArray());
					Assert.assertArrayEquals((c==1 ? positions3active : positions3).toArray(), result.get(2).getPositions().toArray());
				}
			}
		}		
	}
	
	@MethodRef(name = "changeBoxes", signature = "(IIQLinkedList<QTRCRequirement;>;)QLinkedList<QTRCRequirement;>;")
	@Test
	public void setAllClear_testChangeBoxes() throws Exception {
		int positionOfChange = 0;
		int amountOfChange = 0;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		
		// Solution 1:
		
		positionOfChange = 0;
		amountOfChange = 200;

		
		
		LinkedList<int[]> positions1active 	= new LinkedList<int[]>();
		LinkedList<int[]> positions1 		= new LinkedList<int[]>();
		LinkedList<int[]> positions2active	= new LinkedList<int[]>();
		LinkedList<int[]> positions2 		= new LinkedList<int[]>();
		LinkedList<int[]> positions3active 	= new LinkedList<int[]>();
		LinkedList<int[]> positions3 		= new LinkedList<int[]>();
		
		int[] box0 = {0, 200}; // Adjacent
		
		positions1active.add(box0);		
		positions2active.add(box0);
		positions3active.add(box0);
		
		// Test 1
		
		System.out.println("Big Set of Overlapping Boxes");
		
		for(int a = 0; a<=1; a++) {
			for(int b = 0; b<=1; b++) {
				for(int c = 0; c<=1; c++) {
					System.out.println("abc = " + a + " " + b + " " + c );
					reqs = BoxActualizerHelperTestactualizeBoxes.threeRequirementsWithAdjacentAndOverlapping();
					reqs.get(0).setActive(a==1);
					reqs.get(1).setActive(b==1);
					reqs.get(2).setActive(c==1);
					
					result = BoxActualizerHelper.changeBoxes(positionOfChange, amountOfChange, reqs);
					
					Assert.assertArrayEquals((a==1 ? positions1active : positions1).toArray(), result.get(0).getPositions().toArray());
					Assert.assertArrayEquals((b==1 ? positions2active : positions2).toArray(), result.get(1).getPositions().toArray());
					Assert.assertArrayEquals((c==1 ? positions3active : positions3).toArray(), result.get(2).getPositions().toArray());
				}
			}
		}
		
		// Test 2
		
		System.out.println("Big Set of Non Overlapping Boxes");
		
		for(int a = 0; a<=1; a++) {
			for(int b = 0; b<=1; b++) {
				for(int c = 0; c<=1; c++) {
					System.out.println("abc = " + a + " " + b + " " + c );
					reqs = BoxActualizerHelperTestactualizeBoxes.threeRequirementsWithNonOverlappingBoxes();
					reqs.get(0).setActive(a==1);
					reqs.get(1).setActive(b==1);
					reqs.get(2).setActive(c==1);
					
					result = BoxActualizerHelper.changeBoxes(positionOfChange, amountOfChange, reqs);
					
					Assert.assertArrayEquals((a==1 ? positions1active : positions1).toArray(), result.get(0).getPositions().toArray());
					Assert.assertArrayEquals((b==1 ? positions2active : positions2).toArray(), result.get(1).getPositions().toArray());
					Assert.assertArrayEquals((c==1 ? positions3active : positions3).toArray(), result.get(2).getPositions().toArray());
				}
			}
		}
	}
	
	
	
}