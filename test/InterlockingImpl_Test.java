import org.junit.Before;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Arrays;



// ✅ Final test suite for InterlockingImpl

public class InterlockingImpl_Test {



    private InterlockingImpl inter;

    private Train passenger;

    private Train freight;



    @Before

    public void setup() {

        inter = new InterlockingImpl();

        passenger = new Train("P1", Train.Type.PASSENGER);

        freight = new Train("F1", Train.Type.FREIGHT);

    }



    @Test

    public void testAddAndMovePassenger() {

        assertTrue(inter.addTrain("S1", passenger));

        assertTrue(inter.moveTrain("S1", "S2"));

        assertTrue(inter.moveTrainWithLocks("S2", "S4", passenger));

        assertFalse(inter.isFree("S4"));

    }



    @Test

    public void testFreightNormalFlow() {

        assertTrue(inter.addTrain("S3", freight));

        assertTrue(inter.moveTrainWithLocks("S3", "S4", freight));

        assertFalse(inter.isFree("S4"));

    }



    @Test

    public void testPassengerPriorityBlocksFreight() {

        inter.addTrain("S2", passenger);

        inter.addTrain("S3", freight);

        assertFalse("Freight blocked if passenger in S2",

                    inter.moveTrainWithLocks("S3", "S4", freight));

    }



    @Test

    public void testBlockedSection() {

        inter.addTrain("S2", passenger);

        assertFalse(inter.addTrain("S2", freight));

    }



    @Test

    public void testExitTrain() {

        inter.addTrain("S9", freight);

        inter.exitTrain("S9");

        assertTrue(inter.isFree("S9"));

    }



    @Test

    public void testCollisionDetection() {

        inter.addTrain("S2", passenger);

        assertTrue("Collision detected if another tries to enter S2",

                   inter.detectCollision("S2"));

    }



    @Test

    public void testDeadlockDetection() {

        inter.addTrain("S3", passenger);

        inter.addTrain("S5", freight);

        assertTrue("Deadlock should be detected", inter.detectDeadlock());

    }



    @Test

    public void testDeadlockResolve() {

        inter.addTrain("S3", passenger);

        inter.addTrain("S5", freight);

        assertTrue(inter.detectDeadlock());

        inter.resolveDeadlock();

        assertFalse("Locks released after resolve",

                    inter.isCrossingLocked() || inter.isSouthJunctionLocked());

    }



    @Test

    public void testSouthJunctionLock() {

        inter.addTrain("S4", passenger);

        assertTrue(inter.moveTrainWithLocks("S4", "S9", passenger));

        assertTrue(inter.isSouthJunctionLocked());

    }



    @Test

    public void testCrossingLock() {

        inter.addTrain("S2", passenger);

        assertTrue(inter.moveTrainWithLocks("S2", "S4", passenger));

        assertTrue(inter.isCrossingLocked());

    }

}