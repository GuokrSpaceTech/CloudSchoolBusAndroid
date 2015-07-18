package com.guokrspace.cloudschoolbus.parents.module.explore;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.android.support.utils.DateUtils;
import com.dexafree.materialList.cards.CustomCard;
import com.dexafree.materialList.controller.CommonRecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.fastjson.FastJsonTools;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
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
import com.guokrspace.cloudschoolbus.parents.protocols.CloudSchoolBusRestClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

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
    private MaterialListView mMaterialListView;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private String mLocalArticleStartTime;
    private String mLocalArticleEndTime;

    private int previousTotal = 0;
    private int visibleThreshold = 3;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    final private static int MSG_ONREFRESH  = 1;
    final private static int MSG_ONLOADMORE  = 2;
    final private static int MSG_ONCACHE  = 3;
    final private static int MSG_NOCHANGE  = 4;

    private Handler mHandler = new Handler( new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch(msg.what)
            {
                case MSG_ONREFRESH:
                    InsertCardsAtBeginning();
                    if(mSwipeRefreshLayout.isRefreshing())
                        mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case MSG_ONLOADMORE:
                    AppendCards();
                    break;
                case MSG_ONCACHE:
                    AppendCards();
                    break;
                case MSG_NOCHANGE:
                    if(mSwipeRefreshLayout.isRefreshing())
                        mSwipeRefreshLayout.setRefreshing(false);
            }
            return false;
        }
    });

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_article_list, container, false);
        mMaterialListView = (MaterialListView) root.findViewById(R.id.material_listview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                ArticleEntity articleEntity = mArticleEntities.get(0);
                String endtime = articleEntity.getPublishtime();
                UpdateArticlesCacheForward(endtime);
            }
        });

        mLayoutManager = (LinearLayoutManager)mMaterialListView.getLayoutManager();
        mMaterialListView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            private boolean loading = true;

            @Override
            public void onScrollStateChanged(android.support.v7.widget.RecyclerView recyclerView, int newState) {

            }

            @Override
            public void onScrolled(android.support.v7.widget.RecyclerView recyclerView, int dx, int dy) {
                //Log.d("Aing", "dx:" + dx + ", dy:" + dy);

                visibleItemCount = mMaterialListView.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached

                    Log.i("...", "end called");
                    ArticleEntity articleEntity = mArticleEntities.get(mArticleEntities.size()-1);
                    String starttime = articleEntity.getAddtime();
                    UpdateArticlesCacheDownward(starttime);

                    // Do something
//                    new LoadTask(MainActivity.this, start).execute();

                    loading = true;
                }
            }
        });


        GetArticlesFromCache();

        if(mArticleEntities.size() == 0)
            GetLasteArticlesFromServer();
        else
        {
            ArticleEntity articleEntity = mArticleEntities.get(0);
            String endtime = articleEntity.getAddtime();
            UpdateArticlesCacheForward(endtime);
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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

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
        if(mArticleEntities.size()!=0)
            mHandler.sendEmptyMessage(MSG_ONCACHE);
    }

    //Get all articles from newest in Cache to newest in Server
    private void UpdateArticlesCacheForward(String endtime)
    {
        GetArticlesFromServer("0",  endtime);
    }

    //Get the older 20 articles from server then update the cache
    private void UpdateArticlesCacheDownward(String starttime)
    {
        GetArticlesFromServer( starttime,  "0");
    }

    //Get Lastest 20 Articles from server, only used when there is no cache
    private void GetLasteArticlesFromServer()
    {
        GetArticlesFromServer("0", "0");
    }

    private void GetArticlesFromServer( final String starttime, final String endtime)
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
                    articleEntityDao.insertOrReplace(articleEntity);

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
                        imageEntityDao.insertOrReplace(imageEntity);
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
                        tagEntityDao.insertOrReplace(tagEntity);
                    }
                }

                //Update mArticleEntities
                String start = articleList.getArticlelist().get(0).getAddtime();
                String end   = articleList.getArticlelist().get(articleList.getArticlelist().size()-1).getAddtime();
                QueryBuilder queryBuilder = articleEntityDao.queryBuilder();
                queryBuilder.where(ArticleEntityDao.Properties.Addtime.between(end,start));
                mArticleEntities = (ArrayList<ArticleEntity>)queryBuilder.list();

                if(starttime.equals("0") && endtime.equals("0"))
                    mHandler.sendEmptyMessage(MSG_ONREFRESH);
                else if(endtime.equals("0"))
                    mHandler.sendEmptyMessage(MSG_ONLOADMORE);
                else if(starttime.equals("0"))
                    mHandler.sendEmptyMessage(MSG_ONREFRESH);

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
                String retCode = "";
                for (int i = 0; i < headers.length; i++) {
                    Header header = headers[i];
                    if ("code".equalsIgnoreCase(header.getName())) {
                        retCode = header.getValue();
                        break;
                    }
                }
                if (retCode != "-2") {
                    // No New Records are found
                    mHandler.sendEmptyMessage(MSG_NOCHANGE);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }
        });
    }

    private void AppendCards()
    {
        for(int i=0; i<mArticleEntities.size(); i++)
        {
            ArticleEntity articleEntity = mArticleEntities.get(i);
            CustomCard card = new CustomCard(mParentContext);
            card.setTeacherAvatarUrl("https://assets-cdn.github.com/images/modules/logos_page/GitHub-Mark.png");
            card.setTeacherName("小花老师");
            card.setKindergarten("星星幼儿园");
            card.setSentTime(articleEntity.getAddtime());

            card.setTitle(articleEntity.getTitle() + "Title");
            card.setDescription(articleEntity.getContent() + "Test Content: this is a content...");
            card.setDrawable(articleEntity.getImages().get(0).getSource());

            List<TagEntity> tagEntities = articleEntity.getTags();
            TagRecycleViewAdapter adapter = new TagRecycleViewAdapter(tagEntities);
            card.setAdapter(adapter);

            CommonRecyclerItemClickListener tagClickListener = new CommonRecyclerItemClickListener(mParentContext, new CommonRecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Toast.makeText(mParentContext, "haha" + position, Toast.LENGTH_SHORT).show();
                    animation(view);
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            });
            card.setmOnItemSelectedListener(tagClickListener);

            View.OnClickListener shareButtonClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mParentContext, "haha", Toast.LENGTH_SHORT).show();
                }
            };
            card.setmShareButtonClickListener(shareButtonClickListener);

            View.OnClickListener likeButtonClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mParentContext, "haha", Toast.LENGTH_SHORT).show();
                    animation(v);
                }
            };
            card.setmLikeButtonClickListener(likeButtonClickListener);

            View.OnClickListener commentButtonClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mParentContext, "haha", Toast.LENGTH_SHORT).show();
                    animation(v);
                }
            };
            card.setmCommentButtonClickListener(commentButtonClickListener);
            card.setLikesNum(articleEntity.getUpnum());
            card.setCommentNum(articleEntity.getCommentnum());

            mMaterialListView.add(card);
        }
    }

    private void InsertCardsAtBeginning()
    {
        for(int i=mArticleEntities.size()-1; i>=0; i--)
        {
            ArticleEntity articleEntity = mArticleEntities.get(i);
            CustomCard card = new CustomCard(mParentContext);
            card.setTeacherAvatarUrl("https://assets-cdn.github.com/images/modules/logos_page/GitHub-Mark.png");
            card.setTeacherName("小花老师");
            card.setKindergarten("星星幼儿园");
            card.setSentTime(articleEntity.getAddtime());

            card.setTitle(articleEntity.getTitle() + "Title");
            card.setDescription(articleEntity.getContent() + "Test Content: this is a content...");
            card.setDrawable(articleEntity.getImages().get(0).getSource());

            List<TagEntity> tagEntities = articleEntity.getTags();
            TagRecycleViewAdapter adapter = new TagRecycleViewAdapter(tagEntities);
            card.setAdapter(adapter);

            CommonRecyclerItemClickListener tagClickListener = new CommonRecyclerItemClickListener(mParentContext, new CommonRecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Toast.makeText(mParentContext, "haha" + position, Toast.LENGTH_SHORT).show();
                    animation(view);
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            });
            card.setmOnItemSelectedListener(tagClickListener);

            View.OnClickListener shareButtonClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mParentContext, "haha", Toast.LENGTH_SHORT).show();
                }
            };
            card.setmShareButtonClickListener(shareButtonClickListener);

            View.OnClickListener likeButtonClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mParentContext, "haha", Toast.LENGTH_SHORT).show();
                    animation(v);
                }
            };
            card.setmLikeButtonClickListener(likeButtonClickListener);

            View.OnClickListener commentButtonClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mParentContext, "haha", Toast.LENGTH_SHORT).show();
                    animation(v);
                }
            };
            card.setmCommentButtonClickListener(commentButtonClickListener);
            card.setLikesNum(articleEntity.getUpnum());
            card.setCommentNum(articleEntity.getCommentnum());

            mMaterialListView.addAtStart(card);
        }
    }


    public  void animation(View v){
        v.clearAnimation();
        ScaleAnimation animation =new ScaleAnimation(0.0f, 1.4f, 0.0f, 1.4f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(300);
        v.setAnimation(animation);
    }
}