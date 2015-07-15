package com.guokrspace.cloudschoolbus.parents.module.explore;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.support.utils.DateUtils;
import com.dexafree.materialList.cards.BigImageCard;
import com.dexafree.materialList.cards.CustomCard;
import com.dexafree.materialList.view.MaterialListView;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.fastjson.FastJsonTools;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseListFragment;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ArticleEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ArticleEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ImageEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ImageEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TagEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TagEntityDao;
import com.guokrspace.cloudschoolbus.parents.entity.Article;
import com.guokrspace.cloudschoolbus.parents.entity.ArticleList;
import com.guokrspace.cloudschoolbus.parents.entity.ImageFile;
import com.guokrspace.cloudschoolbus.parents.entity.Tag;
import com.guokrspace.cloudschoolbus.parents.module.explore.dummy.DummyContent;
import com.guokrspace.cloudschoolbus.parents.protocols.CloudSchoolBusRestClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ClassUpdatesFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ArrayList<ArticleEntity> mArticleEntities = new ArrayList<ArticleEntity>();
    private MaterialListView         mMaterialListView;
    private ArticlesAdapter          mAdapter;

    // TODO: Rename and change types of parameters
    public static ClassUpdatesFragment newInstance(String param1, String param2) {
        ClassUpdatesFragment fragment = new ClassUpdatesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ClassUpdatesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        GetArticlesFromCache();

        if(mArticleEntities.size() == 0)
            GetLasteArticlesFromServer();
        else
        {
            ArticleEntity articleEntity = mArticleEntities.get(mArticleEntities.size()-1);
            String starttime = articleEntity.getAddtime();
            UpdateArticlesCacheForward(starttime);
        }
//        mAdapter = new ArticlesAdapter(mArticleEntities,mApplication.getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_article_list, container, false);
        mMaterialListView = (MaterialListView)root.findViewById(R.id.material_listview);

//        mMaterialListView.setAdapter(mAdapter);

        for(int i=0; i<mArticleEntities.size(); i++)
        {
            ArticleEntity articleEntity = mArticleEntities.get(i);
            CustomCard card = new CustomCard(mApplication.getApplicationContext());
            card.setTeacherAvatarUrl("https://assets-cdn.github.com/images/modules/logos_page/GitHub-Mark.png");
            card.setTeacherName("小花老师");
            card.setKindergarten("星星幼儿园");
            card.setSentTime(articleEntity.getAddtime());
            card.setTitle(articleEntity.getTitle() + "Title");
            card.setDescription(articleEntity.getContent() + "Test Content: this is a content...");
            card.setDrawable(articleEntity.getImages().get(0).getSource());
            mMaterialListView.add(card);
        }

        return root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
//
//        if (null != mListener) {
//            // Notify the active callbacks interface (the activity, if the
//            // fragment is attached to one) that an item has been selected.
//            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
//        }
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }


    //Get all articles from cache
    private void GetArticlesFromCache()
    {
        final ArticleEntityDao articleEntityDao = mApplication.mDaoSession.getArticleEntityDao();
        mArticleEntities = (ArrayList<ArticleEntity>)articleEntityDao.queryBuilder().list();
    }

    //Get all articles from newest in Cache to newest in Server
    private void UpdateArticlesCacheForward(String starttime)
    {
        String endtime = DateUtils.currentMillisString();
        GetArticlesFromServer(starttime,  endtime);
    }

    //Get the older 20 articles from server then update the cache
    private void UpdateArticlesCacheDownward(String endtime)
    {
        GetArticlesFromServer( "",  endtime);
    }

    //Get Lastest 20 Articles from server, only used when there is no cache
    private void GetLasteArticlesFromServer()
    {
        GetArticlesFromServer("", "");
    }

    private void GetArticlesFromServer(String starttime, String endtime)
    {
        SQLiteDatabase db = mApplication.mDBhelper.getWritableDatabase();
        final ArticleEntityDao articleEntityDao = mApplication.mDaoSession.getArticleEntityDao();
        final ImageEntityDao   imageEntityDao   = mApplication.mDaoSession.getImageEntityDao();
        final TagEntityDao     tagEntityDao     = mApplication.mDaoSession.getTagEntityDao();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("starttime", starttime);
        params.put("endtime", endtime);

        CloudSchoolBusRestClient.get("article", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String retCode = "";
                for (int i = 0; i < headers.length; i++) {
                    Header header = headers[i];
                    if ("code".equalsIgnoreCase(header.getName())) {
                        retCode = header.getValue();
                        break;
                    }
                }
                if (retCode != "1") {
                    // Errro Handling
                }
                ArticleList articleList = (ArticleList)FastJsonTools.getObject(response.toString(), ArticleList.class);

                for(int i=0; i<articleList.getArticlelist().size(); i++)
                {
                    Article article = articleList.getArticlelist().get(i);
                    ArticleEntity articleEntity = new ArticleEntity(
                            article.getArticlekey(),
                            article.getTag(),
                            article.getArticleid(),
                            article.getTitle(),
                            article.getContent(),
                            article.getPublishtime(),
                            article.getAddtime(),
                            article.getUpnum(),
                            article.getCommentnum(),
                            article.getHavezan()
                    );
                    articleEntityDao.insert(articleEntity);

                    for(int j=0; j<article.getPlist().size(); j++)
                    {
                        ImageFile imageFile = article.getPlist().get(j);
                        ImageEntity imageEntity = new ImageEntity(
                                imageFile.getFilename(),
                                imageFile.getSource(),
                                imageFile.getFext(),
                                imageFile.getSize(),
                                imageFile.getIsCloud(),
                                article.getArticlekey());
                        imageEntityDao.insert(imageEntity);
                    }

                    for(int j=0; j<article.getTaglist().size(); j++)
                    {
                        Tag tag = article.getTaglist().get(j);
                        TagEntity tagEntity = new TagEntity(
                                tag.getTagid(),
                                tag.getTagName(),
                                tag.getTagnamedesc(),
                                tag.getTagname_en(),
                                tag.getTagnamedesc_en(),
                                article.getArticlekey());
                        tagEntityDao.insert(tagEntity);
                    }
                }

                //Update mArticleEntities
                mArticleEntities = (ArrayList<ArticleEntity>)articleEntityDao.queryBuilder().list();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }
        });

    }
}