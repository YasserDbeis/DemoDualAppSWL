package com.example.demodualappswl

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import androidx.window.WindowManager
import android.widget.TextView
import androidx.core.util.Consumer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import androidx.window.FoldingFeature
import androidx.window.WindowLayoutInfo
import com.example.demodualappswl.models.Issue
import com.example.demodualappswl.adapters.IssueAdapter
import com.example.demodualappswl.databinding.ActivityMainBinding
import com.example.demodualappswl.viewModels.IssueViewModel
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity(), IssueAdapter.OnItemClickListener {

    private val TAG = "SLIDEPANE"
    lateinit var slidingPane: SlidingPaneLayout
    private lateinit var model: IssueViewModel


    lateinit var issueAdapter: IssueAdapter
    lateinit var issues: MutableList<Issue>
    private lateinit var binding: ActivityMainBinding


    private fun setTodoAdapter(issues: MutableList<Issue>) {
        issueAdapter = IssueAdapter(issues, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        issues = mutableListOf<Issue>()
        model = ViewModelProviders.of(this)[IssueViewModel::class.java]
        setTodoAdapter(issues)

        model.issues.observe(this, { value ->
            issues.clear()
            issues.addAll(value)
            issueAdapter.notifyDataSetChanged()
        })

        binding.rvIssueList.adapter = issueAdapter
        binding.rvIssueList.layoutManager = LinearLayoutManager(this)

        binding.btnAddIssue.setOnClickListener {
            val inputText = binding.tvInputIssue.text

            val issue = Issue(inputText.toString(), inputText.toString())
            var copyIssues = issues.toMutableList()
            copyIssues.add(issue)

            model.setIssues(copyIssues)

            binding.tvInputIssue.text.clear()
        }

        slidingPane = findViewById(R.id.issue_list_screen)

        binding.btnOpenMotionLayout.setOnClickListener{
            intent = Intent(this, TwoPageActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onItemClick(position: Int) {

        slidingPane.open()

        val tvIssueContent = findViewById<TextView>(R.id.tvIssueContent)
        if(tvIssueContent != null) {
            tvIssueContent.text = issues[position].issueContent
        }
    }
}