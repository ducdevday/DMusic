package hcmute.edu.vn.musicplayer.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import hcmute.edu.vn.musicplayer.fragments.ProfileFragment;
import hcmute.edu.vn.musicplayer.fragments.DiscoveryFragment;
import hcmute.edu.vn.musicplayer.fragments.HomeFragment;
import hcmute.edu.vn.musicplayer.fragments.StorageFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new HomeFragment();
            case 1:
                return new DiscoveryFragment();
            case 2:
                return new StorageFragment();
            case 3:
                return new ProfileFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position){
            case 0:
                title="Home";
                break;
            case 1:
                title = "Favorite";
                break;
            case 2:
                title = "Storage";
                break;
            case 3:
                title = "About Us";
                break;
        }
        return title="";
    }
}
