package src.bttest.bluetooth_interface;

public interface IBluetoothManage {
    public void receiveData(byte[] data);
    public void communicationFailure(String cause);
}
