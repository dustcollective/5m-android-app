package com.hanacek.android.utilLib.interfaces;

/**
 * Hooks the lifecycle into the fragments.
 *
 * In the fragments, adapter returns to ViewPager, their lifecycle is not called when shown pageChange. The adapter take care to call methods of
 * ViewPagerFragmentLifecycleInterface to bypass this limitation. It calls it also when fragment is created. To be able to call it when app goes background/foreground
 * the Activity that utilize the adapter has to hook its lifecycle onStart and onStop to the adapter's and adapters have to inherit this interface.
 */
public interface ViewPagerFragmentLifecycleInterface {

    public void onFragmentStart(int position);
    public void onFragmentStop(int position);

}
