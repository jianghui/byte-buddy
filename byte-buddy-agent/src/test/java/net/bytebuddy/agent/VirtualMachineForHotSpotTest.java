package net.bytebuddy.agent;

import net.bytebuddy.test.utility.ObjectPropertyAssertion;
import org.junit.Test;
import org.mockito.InOrder;

import java.io.IOException;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;

public class VirtualMachineForHotSpotTest {

    @Test
    public void testAttachment() throws Exception {
        VirtualMachine.ForHotSpot virtualMachine = spy(new PseudoMachine(
                "0".getBytes("UTF-8"),
                new byte[]{10}
        ));
        virtualMachine.loadAgent("foo", "bar");
        InOrder order = inOrder(virtualMachine);
        order.verify(virtualMachine).connect();
        order.verify(virtualMachine).write("1".getBytes("UTF-8"));
        order.verify(virtualMachine).write(new byte[1]);
        order.verify(virtualMachine).write("load".getBytes("UTF-8"));
        order.verify(virtualMachine).write(new byte[1]);
        order.verify(virtualMachine).write("instrument".getBytes("UTF-8"));
        order.verify(virtualMachine).write(new byte[1]);
        order.verify(virtualMachine).write(Boolean.FALSE.toString().getBytes("UTF-8"));
        order.verify(virtualMachine).write(new byte[1]);
        order.verify(virtualMachine).write("foo=bar".getBytes("UTF-8"));
        order.verify(virtualMachine).write(new byte[1]);
    }

    @Test
    public void testAttachmentWithoutArgument() throws Exception {
        VirtualMachine.ForHotSpot virtualMachine = spy(new PseudoMachine(
                "0".getBytes("UTF-8"),
                new byte[]{10}
        ));
        virtualMachine.loadAgent("foo", null);
        InOrder order = inOrder(virtualMachine);
        order.verify(virtualMachine).connect();
        order.verify(virtualMachine).write("1".getBytes("UTF-8"));
        order.verify(virtualMachine).write(new byte[1]);
        order.verify(virtualMachine).write("load".getBytes("UTF-8"));
        order.verify(virtualMachine).write(new byte[1]);
        order.verify(virtualMachine).write("instrument".getBytes("UTF-8"));
        order.verify(virtualMachine).write(new byte[1]);
        order.verify(virtualMachine).write(Boolean.FALSE.toString().getBytes("UTF-8"));
        order.verify(virtualMachine).write(new byte[1]);
        order.verify(virtualMachine).write("foo".getBytes("UTF-8"));
        order.verify(virtualMachine).write(new byte[1]);
    }

    @Test(expected = IOException.class)
    public void testAttachmentIncompatibleProtocol() throws Exception {
        new PseudoMachine(
                "1".getBytes("UTF-8"),
                "0".getBytes("UTF-8"),
                "1".getBytes("UTF-8"),
                new byte[]{10}
        ).loadAgent("foo", null);
    }

    @Test(expected = IllegalStateException.class)
    public void testAttachmentUnknownError() throws Exception {
        new PseudoMachine(
                "1".getBytes("UTF-8"),
                new byte[]{10},
                "foo".getBytes("UTF-8")
        ).loadAgent("foo", null);
    }

    @Test
    public void testObjectProperties() throws Exception {
        ObjectPropertyAssertion.of(VirtualMachine.ForHotSpot.OnUnix.class).applyBasic();
    }

    private static class PseudoMachine extends VirtualMachine.ForHotSpot {

        private final byte[][] read;

        private int index;

        private PseudoMachine(byte[]... read) {
            super(null);
            this.read = read;
        }

        @Override
        public void detach() throws IOException {
        }

        @Override
        protected void connect() throws IOException {

        }

        @Override
        protected int read(byte[] buffer) throws IOException {
            if (index == read.length) {
                return -1;
            }
            byte[] read = this.read[index++];
            System.arraycopy(read, 0, buffer, 0, read.length);
            return read.length;
        }

        @Override
        protected void write(byte[] buffer) throws IOException {

        }
    }
}