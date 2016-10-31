package edu.lk;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String xx = "e:\\ivy\\ant-ivy\\test\\repositories\\dual-remote\\xerces\\xercesimpl-2.6.2.jar";
		
		SingleJar2mvn s = new SingleJar2mvn(xx);
		String relocateJar2mvn = s.relocateJar2mvn();
		
		System.out.println( relocateJar2mvn + "  --- " + s.getMsg());
	}

}
