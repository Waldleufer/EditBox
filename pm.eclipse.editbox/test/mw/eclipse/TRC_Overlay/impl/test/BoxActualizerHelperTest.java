package mw.eclipse.TRC_Overlay.impl.test;

import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Logger;

import javax.annotation.Generated;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.tools.configuration.base.MethodRef;

import mw.eclipse.TRC_Overlay.impl.BoxActualizerHelper;
import pm.eclipse.editbox.impl.TRCFileInteraction.TRCRequirement;

@Generated(value = "org.junit-tools-1.1.0")
public class BoxActualizerHelperTest {
	
	@Generated(value = "org.junit-tools-1.1.0")
	private Logger logger = Logger.getLogger(BoxActualizerHelperTest.class.toString());

	@Before
	public void setUp() throws Exception {
		
	}

	/**
	 * 
	 * @return one Req with two Boxes: {{0, 20}, {21, 40}}
	 */
	private LinkedList<TRCRequirement> oneRequirementTwoBoxesSeparatedByOneSpace() {
		
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
	public void newBox_testActualizeBoxes() throws Exception {
		
		Random rand = new Random(2406l);
		
		for(int i = 0; i<50; i++) {
			
			int positionOfChange = rand.nextInt();
			int amountOfChange = rand.nextInt();
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
	public void alter_testActualizeBoxes() throws Exception {
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
	public void merge_testActualizeBoxes() throws Exception {
		int positionOfChange = 21;
		int amountOfChange = -1;
		LinkedList<TRCRequirement> reqs = null;
		LinkedList<TRCRequirement> result;
		LinkedList<int[]> positions = new LinkedList<int[]>();
		LinkedList<int[]> positions1 = new LinkedList<int[]>();
		
		// Solution 1, 2:
		
		positionOfChange = 21;
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
		positionOfChange = 21;
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
	public void split_testActualizeBoxes() throws Exception {
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